package com.payment.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.service.entity.Payment;
import com.payment.service.repository.PaymentRepository;
import com.payment.service.service.KafkaProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/payments")
@Log4j2
public class PaymentController {

    //set up a thread pool as per the available cores
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        String uuid = UUID.randomUUID().toString();
        executor.submit(() -> {
            try {
                //simulate payment processing
                log.info("Processing payment: {}", payment);
                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                payment.setId(uuid);
                payment.setStatus("CONFIRMED");
                paymentRepository.save(payment);
                log.info("Payment successful: {}", payment);

            } catch (Exception e) {
                log.error("Error processing payment: {}", e.getMessage());
                payment.setStatus("FAILED");
            } finally {
                try {
                    kafkaProducer.sendMessage(payment);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        return ResponseEntity.accepted().body(uuid);
    }

    /**
     * Long Polling endpoint to get payment by id, with a timeout of 60 seconds
     *
     * @param id Payment ID
     * @return DeferredResult<Payment>
     */
    @GetMapping("/{id}")
    public DeferredResult<Payment> getPayment(@PathVariable String id) {
        log.info("Getting payment with id: {}", id);
        DeferredResult<Payment> deferredResult = new DeferredResult<>(60000L); // 60 seconds timeout

        CompletableFuture.supplyAsync(() -> {
            Payment payment = null;
            int retries = 5; // Number of retries
            int delay = 10 * 1000; // 10 sec Delay between retries in milliseconds

            for (int i = 0; i < retries; i++) {
                payment = paymentRepository.findById(id).orElse(null);
                if (payment != null) {
                    log.info("Payment found: {}", payment);
                    deferredResult.setResult(payment);
                    break;
                }
                log.info("Payment not found. Retrying...");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    deferredResult.setErrorResult(new RuntimeException("Thread was interrupted", e));
                    break;
                }
            }

            if (payment == null) {
                deferredResult.setErrorResult(new RuntimeException("Payment not found within the timeout period"));
            }

            return payment;
        });

        return deferredResult;
    }

    // Add other CRUD endpoints as needed
}

