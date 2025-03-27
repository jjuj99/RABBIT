FROM eclipse-temurin:17-jre-alpine

# 로컬에서 빌드한 jar 복사 (정확한 파일명은 직접 지정해주는 게 안정적)
COPY build/libs/rabbit-server-0.0.1-SNAPSHOT.jar app.jar
COPY . .

# 포트 노출 (application.yml의 server.port와 동일하게)
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
