# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven build output
COPY target/mcp-java-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Optional runtime flags
ENV JAVA_OPTS=""

# Run the app (cleaner, allows signal forwarding, better for containers)
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
