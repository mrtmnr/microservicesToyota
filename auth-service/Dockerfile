FROM --platform=linux/amd64 openjdk:17-alpine
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]