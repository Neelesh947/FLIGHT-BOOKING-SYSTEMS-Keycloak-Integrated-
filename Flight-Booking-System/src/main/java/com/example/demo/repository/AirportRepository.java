package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Airport;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String>{

	Optional<Airport> findByAirportNameAndAirportCodeAndLocation(String airportname, String airportcode, String location);
	
	Optional<Airport> findByAirportCodeAndLocation(String airportCode, String location);
}
