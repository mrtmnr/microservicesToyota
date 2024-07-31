FROM --platform=linux/amd64 openjdk:17-alpine
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8765
CMD ["java","-jar","app.jar"]