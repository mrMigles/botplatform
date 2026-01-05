FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /workspace
COPY .mvn ./.mvn
COPY pom.xml .
COPY src ./src
RUN mvn -B -e -DskipTests clean package

FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY --from=builder /workspace/target/botplatform-0.0.1-SNAPSHOT.jar /app.jar
RUN apk update \
    && apk add --no-cache ca-certificates fontconfig ttf-dejavu \
    && update-ca-certificates \
    && rm -rf /var/cache/apk/*
ENTRYPOINT ["java",
           "--add-opens=java.base/java.lang=ALL-UNNAMED",
           "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
           "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
           "--add-opens=java.base/java.io=ALL-UNNAMED",
           "--add-opens=java.base/java.net=ALL-UNNAMED",
           "--add-opens=java.base/java.util=ALL-UNNAMED",
           "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
           "-Djava.security.egd=file:/dev/./urandom",
           "-jar",
           "/app.jar"]
