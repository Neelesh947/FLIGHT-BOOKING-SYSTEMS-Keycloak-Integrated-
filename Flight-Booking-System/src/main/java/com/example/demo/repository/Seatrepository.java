package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Seat;

@Repository
public interface Seatrepository extends JpaRepository<Seat,String > {

	public Optional<Seat> findBySeatNumber(String seatNumber);
	
	public List<Seat> findByFlightId(String flightId);
}
