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
                    steps {
                        dir('rabbit-server') {
                            withCredentials([
                                string(credentialsId: 'docker-backend-image', variable: 'BACKEND_IMAGE'),
                                usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS'
                                )]) {
                                    sh 'docker build -t "\$BACKEND_IMAGE" .'
                                    sh 'echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin'
                                    sh 'docker push \$BACKEND_IMAGE'
                            }
                        }
                    }
                }

                stage('Load Deployment Env') {
                    steps {
                        withCredentials([file(credentialsId: 'rabbit-server-env', variable: 'DEPLOY_ENV_FILE')]) {
                            // .env 파일 로드해서 현재 쉘에 export
                            sh 'set -a && source $DEPLOY_ENV_FILE && set +a'

                            sh 'echo "[FILE] EC2_NAME=$EC2_NAME, EC2_HOST=$EC2_HOST, SCRIPT=$RABBIT_DEPLOY_SCRIPT"'
                        }
                    }
                }

                stage ('Deploy to S3 & Restart Rabbit Server') {
                    steps {
                        sshagent(credentials: ['rabbit-ec2-key']) {
                            sh """
                                echo '[JENKINS] EC2에 원격 접속하여 배포 스크립트를 실행합니다...'

                                ssh -o StrictHostKeyChecking=no ${EC2_NAME}@${EC2_HOST} '
                                    bash ${DEPLOY_SCRIPT_PATH}
                                '
                            """
                        }
                    }
                }
            }
        }
    }
}