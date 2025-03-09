package com.booking.service.repository;

import com.booking.service.entity.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<Booking> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM booking WHERE id = ?",
                new Object[]{id},
                new BeanPropertyRowMapper<>(Booking.class)
        ).stream().findFirst();
    }

    public Optional<Booking> findAvailableBookingForUpdate(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM booking WHERE id = ? AND status = 'AVAILABLE' FOR UPDATE",
                new Object[]{id},
                new BeanPropertyRowMapper<>(Booking.class)
        ).stream().findFirst();
    }

    public int updateStatusToAvailableIfNotConfirmedAndOlderThan(String status, LocalDateTime timeLimit) {
        return jdbcTemplate.update(
                "UPDATE booking SET status = 'AVAILABLE', updated_at = CURRENT_TIMESTAMP WHERE status != ? AND updated_at < ?",
                status, timeLimit
        );
    }

    public void updateBookingStatus(Long bookingId, String confirmed) {
        jdbcTemplate.update(
                "UPDATE booking SET status = ? WHERE id = ?",
                confirmed, bookingId
        );
    }

    public void updateBookingStatusAndPaymentId(Long bookingId, String confirmed, String paymentId) {
        jdbcTemplate.update(
                "UPDATE booking SET status = ?, payment_id = ? WHERE id = ?",
                confirmed, paymentId, bookingId
        );
    }
}