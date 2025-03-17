package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.FlightManagerAndSFlightMappings;

@Repository
public interface FlightManagerAndFlightMappingRepository extends JpaRepository<FlightManagerAndSFlightMappings, String>{

	List<FlightManagerAndSFlightMappings> findByFlightManagerId(String flightManagerId);
	
	public void deleteByFlightId(String flightId);

}
