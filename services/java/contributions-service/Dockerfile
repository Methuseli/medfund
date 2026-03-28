FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY shared/build.gradle.kts shared/
COPY shared/src shared/src
COPY rules-engine/build.gradle.kts rules-engine/
COPY rules-engine/src rules-engine/src
COPY contributions-service/build.gradle.kts contributions-service/
COPY contributions-service/src contributions-service/src
RUN chmod +x gradlew && ./gradlew :contributions-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/contributions-service/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
