FROM openjdk:21-ea-slim

COPY notification-service-standalone.jar .

ENTRYPOINT ["java", "-jar", "notification-service-standalone.jar"]