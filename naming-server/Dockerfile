FROM --platform=linux/amd64 openjdk:17-alpine
COPY target/naming-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
CMD ["java","-jar","app.jar"]