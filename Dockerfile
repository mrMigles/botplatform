FROM eclipse-temurin:8-jdk

VOLUME /tmp
COPY target/botplatform-0.0.1-SNAPSHOT.jar app.jar

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates fontconfig fonts-dejavu-core \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
