package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight, String>{

	public void deleteByDepartureTimeBefore(LocalDateTime departureTime);
	
	public List<Flight> findByFromLocationAndToLocation(String departureLocation, String arrivalLocation);
}
