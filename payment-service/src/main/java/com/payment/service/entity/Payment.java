package com.payment.service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;


@Getter
@Setter
@ToString
@AllArgsConstructor
public class Payment {

    @Id
    private String id;
    private Long bookingId;
    private Double amount;
    private String status;
    private String cardNumber;
    private String cvv;
    private String cardHolder;
    private String expiryDate;
}
