# Use OpenJDK as base image
FROM openjdk:17-jdk-slim
LABEL authors="majid"
# Set working directory
WORKDIR /app

# Copy JAR file (Ensure you run mvn package first)
COPY target/booking-service.jar booking-service.jar

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "booking-service.jar"]
