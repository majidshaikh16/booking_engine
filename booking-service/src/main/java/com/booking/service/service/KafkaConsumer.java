package com.booking.service.service;

import com.booking.service.repository.BookingRepository;
import com.booking.service.repository.BookingRepositoryI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class KafkaConsumer {
    @Autowired
    private BookingRepository bookingRepository;

    @KafkaListener(topics = "payment-updates", groupId = "payment-updates-service-group")
    public void consumeMessage(String message) {
        log.info("Received a payment update message: {}", message);

        // Parse the JSON string to a Map
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> paymentResponse = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {
            });
            // Process the payment map (for simplicity, we’ll just print the map)
            log.info("Parsed payment response: {}", paymentResponse);

            Object bookingId = paymentResponse.getOrDefault("bookingId", null);
            Object paymentId = paymentResponse.getOrDefault("id", null);
            Object status = paymentResponse.getOrDefault("status", null);

            if (null == bookingId || null == paymentId || null == status) {
                log.info("Booking ID = {} or Payment Id = {} not found in payment response", bookingId, paymentId);
            } else {
                // Update the booking status and payment id in the database
                bookingRepository.updateBookingStatusAndPaymentId(Long.parseLong(String.valueOf(bookingId)), (String) status, (String) paymentResponse.get("id"));
                log.info("Payment status {} for booking ID = {}", status, bookingId);
            }
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }
}
