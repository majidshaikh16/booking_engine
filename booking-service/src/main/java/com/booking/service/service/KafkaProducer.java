package com.booking.service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "payment-events";

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
