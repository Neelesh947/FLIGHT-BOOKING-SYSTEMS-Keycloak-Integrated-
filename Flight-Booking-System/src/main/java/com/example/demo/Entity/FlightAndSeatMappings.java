package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "flight_and_seat_mappings")
public class FlightAndSeatMappings extends BaseEntity{

	@Column(nullable = false)
	private String FlightId;
	
	@Column(nullable = false)
	private String SeatId;
}
