package com.booking.service.service;

import com.booking.service.entity.Booking;
import com.booking.service.repository.BookingRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class BookingService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        schedulePaymentCheck();
    }

    /**
     * We wont to first check if the hotel id for the booking exists and is not booked already
     * We would want to book it for some time and then release it if the payment is not successful
     * We would then want to check if the payment was succcessful before we can confirm the booking or not
     * we need to rollback if the payment has failed, that means making the hotel available again
     */
    //we need first the method which take the booking boject and check if the hotel is available using select for update query of posgress
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public Booking createBooking(Booking booking) {
        Optional<Booking> existingBooking = bookingRepository.findAvailableBookingForUpdate(booking.getId());

        if (!existingBooking.isPresent()) {
            return null;
        }
        bookingRepository.updateBookingStatus(booking.getId(), "PENDING PAYMENT");
        return bookingRepository.findById(booking.getId()).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public String createBookingJdbc(Booking booking) {
        Long existingBooking = jdbcTemplate.query(
                "SELECT id FROM booking WHERE id = ? AND status = 'AVAILABLE' FOR UPDATE",
                new Object[]{booking.getId()},
                (rs, rowNum) -> rs.getLong("id")
        ).stream().findFirst().orElse(null);

        if (existingBooking == null) {
            return "Booking entity not found Or The Entity is already booked";
        }
        // update the booking status to PENDING PAYMENT and save the booking
        jdbcTemplate.update("UPDATE booking SET status = 'PENDING PAYMENT' WHERE id = ?", existingBooking);

        return "redirect:/payment?bookingId=" + existingBooking;
    }


    //schedule a payment check
    //TODO setup refund if the payment was done after the X allowed time duration for payment
    private void schedulePaymentCheck() {
        //lets set a timer to check the payment status after X minutes, we set for 2 min as an example
        //TODO keep the delay configurable
        scheduler.scheduleAtFixedRate(() -> updateBookingToAvailableIfNotConfirmed("CONFIRMED"), 2, 2, TimeUnit.MINUTES);
    }

    private void updateBookingToAvailableIfNotConfirmed(String otherThanStatus) {
        log.info("Checking payment otherThanStatus for all bookings with otherThanStatus {}", otherThanStatus);
        // Calculate the time limit (2 minutes ago)
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(2);

        // Update the booking otherThanStatus if the updatedAt is older than 2 minutes
        int rowUpdated = bookingRepository.updateStatusToAvailableIfNotConfirmedAndOlderThan(otherThanStatus, timeLimit);
        log.info("Updated otherThanStatus to AVAILABLE for total rowCount {}", rowUpdated);
    }
}
