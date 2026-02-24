# --- Build Stage ----
FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set permissions
RUN chown appuser:appgroup app.jar
USER appuser

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]