FROM eclipse-temurin:17-jre
LABEL authors="alexsey"
WORKDIR /tmp
COPY target/auth-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]