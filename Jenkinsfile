pipeline {
    agent any
    
    tools {
        nodejs 'NodeJS'
    }

    stages {
        stage('Detect Changes') {
            steps {
                script {
                    // 변경된 파일 목록 가져오기
                    def changes = sh(script: "git diff --name-only HEAD^ HEAD", returnStdout: true).trim()
                    
                    // 프론트엔드/백엔드 변경 여부 확인
                    env.FRONTEND_CHANGED = changes.contains('rabbit-client/') ? 'true' : 'false'
                    env.BACKEND_CHANGED = changes.contains('rabbit-server/') ? 'true' : 'false'

                    echo "Frontend changed: ${env.FRONTEND_CHANGED}"
                    echo "Backend changed: ${env.BACKEND_CHANGED}"
                }
            }
        }
        
        // 프론트엔드 배포 단계
        stage('Deploy Frontend') {
            when {
                expression { return env.FRONTEND_CHANGED == 'true' }
            }
            stages {
                stage('Setup pnpm') {
                    steps {
                        sh 'npm install -g pnpm'
                    }
                }
                
                stage('Setup Environment') {
                    steps {
                        withCredentials([file(credentialsId: 'rabbit-client-env', variable: 'ENV_FILE')]) {
                            sh 'cp $ENV_FILE rabbit-client/.env.production'
                        }
                    }
                }
                
                stage('Build') {
                    steps {
                        dir('rabbit-client') {
                            sh 'pnpm install'
                            sh 'pnpm run build:prod'
                        }
                    }
                }
                
                stage('Deploy to S3') {
                    steps {
                        withAWS(credentials: 'clapsheepIAM', region: 'ap-northeast-2') {
                            s3Upload(
                                bucket: 'rabbit-client',
                                path: '',
                                workingDir: 'rabbit-client/dist',
                                includePathPattern: '**/*',
                            )
                        }
                    }
                }
                
                stage('Invalidate CloudFront') {
                    steps {
                        withAWS(credentials: 'clapsheepIAM', region: 'ap-northeast-2') {
                            cfInvalidate(
                                distribution: 'EZETHFN53S9JV',
                                paths: ['/*']
                            )
                        }
                    }
                }
            }
        }

        // 백엔드 배포 단계
        stage('Deploy Backend') {
            when {
                expression { return env.BACKEND_CHANGED == 'true' }
            }
            stages {
                stage('Gradle bootJar') {
                    steps {
                        dir('rabbit-server') {
                            sh 'chmod +x ./gradlew'
                            sh './gradlew --no-daemon clean bootJar'
                        }
                    }
                }

                stage('Docker Build & Push') {
                    agent {
                        docker {
                            image 'docker:dind'
                            args '-v /var/run/docker.sock:/var/run/docker.sock'
                        }
                    }
                    steps {
                        dir('rabbit-server') {
                            withCredentials([string(credentialsId: 'docker-backend-image', variable: 'BACKEND_IMAGE')]) {  
                                script {
                                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-creds') {
                                        def image = docker.build(BACKEND_IMAGE)
                                        image.push()
                                    }
                                }   
                            }
                        }
                    }
                }

                stage('Load Deployment Env') {
                    steps {
                        withCredentials([file(credentialsId: 'rabbit-server-env', variable: 'DEPLOY_ENV_FILE')]) {
                            // env 파일에서 필요한 변수들을 Jenkins 환경 변수로 설정
                            script {
                                def props = readProperties file: env.DEPLOY_ENV_FILE
                                env.EC2_NAME = props.EC2_NAME
                                env.EC2_HOST = props.EC2_HOST
                                env.RABBIT_DEPLOY_SCRIPT = props.RABBIT_DEPLOY_SCRIPT
                            }
                            sh '[FILE] env 파일을 설정했습니다...'
                        }
                    }
                }

                stage ('Deploy to S3 & Restart Rabbit Server') {
                    steps {
                        sshagent(credentials: ['rabbit-ec2-key']) {
                            sh 'echo "[SERVER] EC2에 원격 접속하여 배포 스크립트를 실행합니다..."'
                            sh "ssh -o StrictHostKeyChecking=no ${env.EC2_NAME}@${env.EC2_HOST} 'bash ${env.RABBIT_DEPLOY_SCRIPT}'"
                        }
                    }
                }
            }
        }
    }
}