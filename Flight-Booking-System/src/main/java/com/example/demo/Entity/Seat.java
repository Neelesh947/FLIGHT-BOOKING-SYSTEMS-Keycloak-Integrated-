package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId; // Primary Key
    
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;  // Reference to the Flight entity

    @Column(nullable = false)
    private String seatNumber; // e.g., "1A", "1B"

    @Column(nullable = false)
    private boolean isAvailable; // Indicates if the seat is available

    @Column(nullable = false)
    private String seatClass; // e.g., Economy, Business, First Class

    // Additional attributes can be added as needed
}
