FROM openjdk:17-jdk-slim
LABEL authors="majid"
WORKDIR /app
COPY target/payment-service.jar payment-service.jar
EXPOSE 8082
CMD ["java", "-jar", "payment-service.jar"]
