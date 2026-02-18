# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy only Gradle metadata first for better layer caching
COPY gradle gradle
COPY gradlew settings.gradle build.gradle ./
COPY app app

# Build the app jar
RUN ./gradlew :app:clean :app:build --no-daemon

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the generated jar from the build stage
COPY --from=build /workspace/app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "/app/app.jar"]
