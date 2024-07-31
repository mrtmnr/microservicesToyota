FROM --platform=linux/amd64 openjdk:17-alpine
COPY target/management-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
CMD ["java","-jar","app.jar"]