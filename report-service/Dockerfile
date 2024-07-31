FROM --platform=linux/amd64 openjdk:17-alpine
COPY target/report-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
CMD ["java","-jar","app.jar"]