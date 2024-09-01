FROM openjdk:17-jdk-slim

COPY notification-service-standalone.jar .

ENTRYPOINT ["java", "-jar", "notification-service-standalone.jar"]