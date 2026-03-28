FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY shared/build.gradle.kts shared/
COPY shared/src shared/src
COPY finance-service/build.gradle.kts finance-service/
COPY finance-service/src finance-service/src
RUN chmod +x gradlew && ./gradlew :finance-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/finance-service/build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
