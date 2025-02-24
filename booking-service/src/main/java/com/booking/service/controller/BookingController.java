package com.booking.service.controller;

import com.booking.service.entity.Booking;
import com.booking.service.repository.BookingRepository;
import com.booking.service.service.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        // Save booking to DB
        Booking savedBooking = bookingRepository.save(booking);

        // Send message to Kafka
        kafkaProducer.sendMessage("Booking created with ID: " + savedBooking.getId());

        return savedBooking;
    }

    @GetMapping
    public List<Booking> getBookings() {
        return bookingRepository.findAll();
    }


    // Add other CRUD endpoints as needed
}

