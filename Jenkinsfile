pipeline {
    agent any
    
    tools {
        nodejs 'NodeJS'
    }

    environment {
        BACKEND_IMAGE = 'yueonq/rabbit-server:latest'
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
                            sh "docker build -t ${env.BACKEND_IMAGE} ."

                            withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                                sh """
                                    echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
                                    docker push ${env.BACKEND_IMAGE}
                                """
                            }
                        }
                    }
                }

                stage('Restart Rabbit Server (compose based)') {
                    steps {
                        sh "docker pull ${env.BACKEND_IMAGE}"
                        sh "docker stop rabbit-server || true"
                        sh "docker rm rabbit-server || true"

                        dir('/home/ubuntu/rabbit-docker') {
                            sh "docker compose up -d rabbit-server"
                        }
                    }
                }
            }
        }
    }
}