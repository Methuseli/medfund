FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY shared/build.gradle.kts shared/
COPY shared/src shared/src
COPY rules-engine/build.gradle.kts rules-engine/
COPY rules-engine/src rules-engine/src
COPY claims-service/build.gradle.kts claims-service/
COPY claims-service/src claims-service/src
RUN chmod +x gradlew && ./gradlew :claims-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/claims-service/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
