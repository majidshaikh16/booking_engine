package com.payment.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.service.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String TOPIC = "payment-updates";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(Payment payment) throws JsonProcessingException {
        kafkaTemplate.send(TOPIC, getJsonPayload(payment));
    }

    private String getJsonPayload(Payment payment) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(payment);
        } catch (JsonProcessingException e) {
            throw e;
        }
    }
}
