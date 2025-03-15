package com.example.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Airport;
import com.example.demo.Entity.Flight;
import com.example.demo.Entity.FlightManagerAndSFlightMappings;
import com.example.demo.Entity.Seat;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.repository.AirportRepository;
import com.example.demo.repository.FlightManagerAndFlightMappingRepository;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.Seatrepository;
import com.example.demo.service.FlightService;
import com.exception.model.DataUnavailable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlightServiceImpl implements FlightService{

	@Autowired
	private FlightRepository flightRepository;
	
	@Autowired
	private AirportRepository airportRepository;
	
	@Autowired
	private Seatrepository seatrepository;
	
	@Autowired
	private FlightManagerAndFlightMappingRepository flightManagerAndFlightMappingRepository;

	/**
	 * create Flight
	 * Mapping seat
	 * Mapping airport
	 * Mapping Flight Manager
	 */
	public Flight createFlightService(Flight flight, String flightManagerId, String realm) {
		log.info("Created flight invoked: {}", flight);
		validateFlightDuringCreation(flight, realm);
		mapAirportToTheFlight(flight, realm);
		mapSeatToFlight(flight);
		flight.setId(UUID.randomUUID().toString());
		Flight savedFlight = flightRepository.save(flight);
		String flightId = savedFlight.getId();
		if(flightId == null) {
			throw new DataUnavailable(ErrorConstants.NOT_FOUND);
		}
		FlightManagerAndSFlightMappings flightManagerAndSFlightMappings = mapFlightManagerToFlight(flightManagerId, savedFlight, realm);
		flightManagerAndFlightMappingRepository.save(flightManagerAndSFlightMappings);
		return savedFlight;
	}
	
	private boolean validateFlightDuringCreation(Flight flight, String realm) {
		return true;
	}
	
	private void mapAirportToTheFlight(Flight flight, String realm) {
		Optional<Airport> departureAirport = airportRepository.findByAirportCode(flight.getFromLocation());
		Optional<Airport> arrivalAirport = airportRepository.findByAirportCode(flight.getToLocation());
		departureAirport.ifPresent(airport -> flight.setFromLocation(airport.getAirportCode()));
		arrivalAirport.ifPresent(airport -> flight.setToLocation(airport.getAirportCode()));
	}
	
	private void mapSeatToFlight(Flight flight) {
		List<Seat> seatList = seatrepository.findByFlightId(flight.getId());
		flight.setSeats(seatList);
	}
	
	private FlightManagerAndSFlightMappings mapFlightManagerToFlight(String FlightManagerId, Flight flight, String realm) {
		FlightManagerAndSFlightMappings mappings = new FlightManagerAndSFlightMappings();
		mappings.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		mappings.setUpdateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		mappings.setFlightId(flight.getId());
		mappings.setFlightManagerId(FlightManagerId);
		return mappings;
	}
}
