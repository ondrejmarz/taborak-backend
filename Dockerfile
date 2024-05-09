FROM openjdk:19-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
COPY src/main/resources/ServiceAccountKey.json ServiceAccountKey.json
ENTRYPOINT ["java","-jar","/app.jar"]