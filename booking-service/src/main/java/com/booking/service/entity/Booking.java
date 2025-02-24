package com.booking.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long hotelId;
    private String checkInDate;
    private String checkOutDate;
    private String status;

    // Getters and setters
}
