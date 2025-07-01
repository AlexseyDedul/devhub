FROM eclipse-temurin:17-jre
LABEL authors="alexsey"
WORKDIR /tmp
COPY target/gateway-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]