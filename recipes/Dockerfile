# syntax=docker/dockerfile:1
FROM openjdk:11 as build
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src
RUN ./gradlew build -x test

FROM openjdk:11
VOLUME /tmp
COPY build/libs/recipes-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]