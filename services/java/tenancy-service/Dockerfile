FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY shared/build.gradle.kts shared/
COPY shared/src shared/src
COPY tenancy-service/build.gradle.kts tenancy-service/
COPY tenancy-service/src tenancy-service/src
RUN chmod +x gradlew && ./gradlew :tenancy-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/tenancy-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
