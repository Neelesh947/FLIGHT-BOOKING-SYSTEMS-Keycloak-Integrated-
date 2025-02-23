package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.FlightOperationManagerAndAirportMappings;

@Repository
public interface FlightOperationManagerAndAirportMappingRepository extends JpaRepository<FlightOperationManagerAndAirportMappings, String>{

	List<FlightOperationManagerAndAirportMappings> findByFlightmanagerId(String flightmanagerId);
}
