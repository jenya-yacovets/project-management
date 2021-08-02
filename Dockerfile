FROM openjdk:15
COPY target/*.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
