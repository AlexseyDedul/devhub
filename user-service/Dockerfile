FROM eclipse-temurin:17-jre
LABEL authors="alexsey"
WORKDIR /tmp
COPY target/user-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]