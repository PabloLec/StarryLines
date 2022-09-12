FROM openjdk:19-jdk-alpine3.16

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN $GITHUB_TOKEN

COPY ./src /app/src
COPY ./gradle /app/gradle
COPY ./gradlew /app/gradlew
COPY ./gradle.properties /app/gradle.properties
COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY docker/downloadSchema.sh /app/downloadSchema.sh
COPY docker/entrypoint.sh /app/entrypoint.sh

WORKDIR /app

RUN chmod +x ./entrypoint.sh
RUN chmod +x ./downloadSchema.sh

ENTRYPOINT ["./entrypoint.sh"]