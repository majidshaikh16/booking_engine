package com.booking.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLUpdate;

import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long hotelId;
    private Long roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentId;

    public Booking(long id, String status, LocalDateTime updatedAt) {
        this.id = id;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
