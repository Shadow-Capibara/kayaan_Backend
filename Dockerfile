# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application, skipping tests to speed up the process and avoid environment issues
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Environment variables should be passed during runtime (e.g., via Railway/Render dashboard)
# ENV SPRING_DATASOURCE_URL=...
# ENV SPRING_DATASOURCE_PASSWORD=...

ENTRYPOINT ["java", "-jar", "app.jar"]
