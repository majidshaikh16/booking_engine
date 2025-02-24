package com.payment.service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Getter
@Setter
public class Payment {

    @Id
    private String id;
    private Long bookingId;
    private Double amount;
    private String status;

    // Getters and setters
}

