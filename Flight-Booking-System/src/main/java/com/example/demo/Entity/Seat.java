package com.example.demo.Entity;

import com.example.demo.Enum.SeatClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seats")
public class Seat extends BaseEntity{

    @Column(nullable = false)
    private String seatNumber; // e.g., "1A", "1B"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatClass seatClass; // e.g., Economy, Business, First Class

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
    
    // Additional attributes can be added as needed
}
