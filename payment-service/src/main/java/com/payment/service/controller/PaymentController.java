package com.payment.service.controller;

import com.payment.service.entity.Payment;
import com.payment.service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentRepository.save(payment);
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable String id) {
        return paymentRepository.findById(id).orElse(null);
    }

    // Add other CRUD endpoints as needed
}

