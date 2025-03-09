package com.booking.service.controller;

import com.booking.service.entity.Booking;
import com.booking.service.repository.BookingRepositoryI;
import com.booking.service.service.BookingService;
import com.booking.service.service.KafkaProducer;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Log4j2
public class BookingController {

    @Autowired
    private BookingRepositoryI bookingRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private BookingService bookingService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;
    @PostConstruct
    public void init() {
        log.info("DB URL:::::::::::::::::::: {}", dbUrl);
        log.info("Kafka Server:::::::::::::::::::: {}", kafkaServer);
    }

    @PostMapping("/add/new")
    public Booking addSampleBooking(@RequestBody Booking booking) {
        // Save a new booking to DB
        booking.setStatus("AVAILABLE");
        Booking savedBooking = bookingRepository.save(booking);

        // Send message to Kafka
        kafkaProducer.sendMessage("Booking created with ID: " + savedBooking.getId());

        return savedBooking;
    }

    //Make a booking
    @PostMapping("/book")
    public ResponseEntity<?> bookHotel(@RequestBody Booking booking) {
        // lets call the booking service to book the hotel and then redirect the caller to the payment page
        //res.redirect(`/payment?bookingId=${bookingResponse.data.id}`); example of how to redirect in nodejs
        Booking response = bookingService.createBooking(booking);
        if(response == null){
            return ResponseEntity.badRequest().body("Booking entity not found Or The Entity is already booked");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<Booking> getBookings() {

        return bookingRepository.findAll();
    }


    // Add other CRUD endpoints as needed
}

