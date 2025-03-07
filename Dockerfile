# Use a base image with Java 17 (or your desired version)
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from your build directory to the container
COPY target/*.jar app.jar

# Expose the port your Spring Boot app runs on (usually 8080)
EXPOSE 8080

# Set environment variables (if needed)
# ENV VARIABLE_NAME=value

# Command to run the application
CMD ["java", "-jar", "app.jar"]