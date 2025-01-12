package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.Entity.Seat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {
    private String flightId;
    private String airline;
    private String from; // Departure airport code
    private String to;   // Arrival airport code
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private int availableSeats;
    private List<Seat> seats;
    private int totalSeat;
    private String flightNumber;
}

