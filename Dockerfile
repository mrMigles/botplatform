FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/botplatform-0.0.1-SNAPSHOT.jar app.jar
RUN apk update && apk add fontconfig ttf-dejavu && rm -rf /var/cache/apk/*
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]