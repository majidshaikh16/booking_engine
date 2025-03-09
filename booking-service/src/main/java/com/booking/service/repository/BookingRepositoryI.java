package com.booking.service.repository;

import com.booking.service.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepositoryI extends JpaRepository<Booking, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.status = 'AVAILABLE'")
    Optional<Booking> findAvailableBookingForUpdate(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = 'AVAILABLE', b.updatedAt = CURRENT_TIMESTAMP WHERE b.status = :status AND b.updatedAt < :timeLimit")
    int updateStatusIfOlderThan(@Param("status") String status, @Param("timeLimit") LocalDateTime timeLimit);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = :confirmed WHERE b.id = :bookingId")
    void updateBookingStatus(@Param("bookingId") Long bookingId, @Param("confirmed") String confirmed);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = :confirmed, b.paymentId = :id WHERE b.id = :bookingId")
    void updateBookingStatusAndPaymentId(@Param("bookingId") Long bookingId, @Param("confirmed") String confirmed, @Param("id") String id);
}