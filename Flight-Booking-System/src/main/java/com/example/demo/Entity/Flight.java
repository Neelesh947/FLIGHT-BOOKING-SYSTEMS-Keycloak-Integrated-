package com.example.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.FlightDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flights")
public class Flight {

	  	@Id
	    private String flightId; // Primary Key

	    @Column(nullable = false)
	    private String airline;
	    
	    @Column(nullable = false)
	    private String flightNumber;

	    @Column(nullable = false)
	    private String fromLocation; // Departure airport code

	    @Column(nullable = false)
	    private String toLocation;   // Arrival airport code

	    @Column(nullable = false)
	    private LocalDateTime departureTime;

	    @Column(nullable = false)
	    private LocalDateTime arrivalTime;

	    @Column(nullable = false)
	    private double price;
	    
	    
	    @Column(nullable = false)
	    private int totalSeat; 
	    
	    private int availableSeats;
	    
	    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<Seat> seats;   // List of seats associated with the flight

	    // Method to check if a flight is available
	    public boolean isAvailable() {
	        return availableSeats > 0;
	    }
	    
	    public FlightDto getFlightDto() {
	    	FlightDto dto=new FlightDto();
	    	dto.setFlightId(flightId);
	    	dto.setAirline(airline);
	    	dto.setFrom(fromLocation);
	    	dto.setTo(toLocation);
	    	dto.setDepartureTime(departureTime);
	    	dto.setArrivalTime(arrivalTime);
	    	dto.setAirline(airline);
	    	dto.setAvailableSeats(availableSeats);
	    	dto.setPrice(price);
	    	dto.setSeats(seats);
	    	dto.setTotalSeat(totalSeat);
	    	dto.setFlightNumber(flightNumber);
	    	return dto;
	    }
}
