package com.example.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flights")
public class Flight extends BaseEntity{

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
	    
	    private boolean isEnabled;
	    
	    
	    @Column(nullable = false)
	    private int totalSeat; 
	    
	    private int availableSeats;
	    
	    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<Seat> seats;   // List of seats associated with the flight

	    // Method to check if a flight is available
	    public boolean isAvailable() {
	        return availableSeats > 0;
	    }
	    
	    @ManyToOne
	    @JoinColumn(name = "airport_id")
	    @JsonBackReference
	    private Airport airport;
	    

}
