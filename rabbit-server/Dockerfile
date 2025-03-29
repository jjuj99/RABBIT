# ARM 아키텍처를 지원하는 이미지로 변경하거나 플랫폼 명시
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

# 로컬에서 빌드한 jar 복사
COPY build/libs/rabbit-server-0.0.1-SNAPSHOT.jar app.jar
COPY . .

# 포트 노출 (application.yml의 server.port와 동일하게)
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]