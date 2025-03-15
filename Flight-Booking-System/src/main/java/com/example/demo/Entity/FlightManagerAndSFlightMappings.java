package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flight_manager_and_flight_mappings")
public class FlightManagerAndSFlightMappings extends BaseEntity{
	
	private String flightManagerId;
	
	private String flightId;

}
