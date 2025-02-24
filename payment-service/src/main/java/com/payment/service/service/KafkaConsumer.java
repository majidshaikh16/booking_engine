package com.payment.service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "payment-events", groupId = "payment-service-group")
    public void consumeMessage(String message) {
        System.out.println("Received message from Kafka: " + message);
        // Process the payment (for simplicity, we’ll just print the message)
    }
}
