package com.example.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Airport;
import com.example.demo.Entity.Flight;
import com.example.demo.Entity.FlightManagerAndSFlightMappings;
import com.example.demo.Entity.Seat;
import com.example.demo.Utils.Constants;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.repository.AirportRepository;
import com.example.demo.repository.FlightManagerAndFlightMappingRepository;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.Seatrepository;
import com.example.demo.service.FlightService;
import com.exception.model.DataUnavailable;
import com.exception.model.InternalServerError;
import com.exception.model.UnauthorizedRequest;

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
		flight.setAirport(departureAirport.get());
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
		mappings.setId(UUID.randomUUID().toString());
		return mappings;
	}

	/**
	 * get list of flight
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<Map> getListOfFlight(String flightManagerId, Map<String, Object> allParams, Pageable pageable,
			String realm) {
		log.info("Get list of flight invoked with params: {}", allParams.toString());
		try {
			List<Flight> allFlight = flightRepository.findAll();
			List<FlightManagerAndSFlightMappings> linkedFlight = listOfFlightOperationManagerAndFlightMapping(flightManagerId);
			List<Flight> listOfLinkedFlight = allFlight
								.stream()
								.filter(flight -> linkedFlight
										.stream()
										.anyMatch(mapping -> mapping.getFlightId().equals(flight.getId())))
								.collect(Collectors.toList());
			List<Flight> sortedFlightList = listOfLinkedFlight
								.stream()
								.sorted(Comparator.comparing(Flight::getCreateDateTime).reversed())
								.collect(Collectors.toList());
			if(sortedFlightList.size() < 1) {
				throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
			}
			if(allParams.containsKey(Constants.IS_HIDDEN) &&  !allParams.containsKey(Constants.SEARCH_STRING)) {
				sortedFlightList = allParams.get(Constants.IS_HIDDEN).equals(Constants.TRUE) ? sortedFlightList.stream()
						.filter(Flight -> Flight.isEnabled() == Boolean.TRUE)
						.collect(Collectors.toList()) : sortedFlightList.stream()
						.filter(Flight -> Flight.isEnabled() == Boolean.FALSE)
						.collect(Collectors.toList());
			}
			List<Map> flightList = sortedFlightList.stream().map(flight -> {
				Map<String, Object> map = generateFlightMap(flight);
				return map;
			}).collect(Collectors.toList());
			int totalElement = flightList.size();
			int fromIndex = pageable.getPageNumber() * pageable.getPageSize();
			int toIndex = pageable.getPageSize() + fromIndex;
			return new PageImpl<>(pageable.getPageNumber() < Math.ceil((double) totalElement / (double) pageable.getPageSize())
					? flightList.subList(fromIndex, toIndex > totalElement ? totalElement : toIndex )
							: new ArrayList<>(), pageable, totalElement);
		} catch(Exception e) {
			log.info("Some exception occurred", e.getMessage() , e);
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Map<String , Object> generateFlightMap(Flight flight){
		Map<String, Object> map = new HashMap<>();
	    map.put(Constants.ID, flight.getId());
	    map.put(Constants.CREATED_DATE, flight.getCreateDateTime());
	    map.put(Constants.UPDATED_DATE, flight.getUpdateDateTime());
	    map.put(Constants.AIRLINE, flight.getAirline());
	    map.put(Constants.ARRIVAL_TIME, flight.getArrivalTime());
	    map.put(Constants.AVAILABLE_SEAT, flight.getAvailableSeats());
	    map.put(Constants.DEPARTURE_TIME, flight.getDepartureTime());
	    map.put(Constants.FLIGHT_NUMBER, flight.getFlightNumber());
	    map.put(Constants.FROM_LOCATION, flight.getFromLocation());
	    map.put(Constants.PRICE, flight.getPrice());
	    map.put(Constants.TO_LOCATION, flight.getToLocation());
	    map.put(Constants.TOTAL_SEAT, flight.getTotalSeat());
	    
	    Map<String, Object> airport = new HashMap<>();
	    airport.put(Constants.AIRPORT_ID, flight.getAirport().getId());
	    airport.put(Constants.AIRPORT_NAME, flight.getAirport().getAirportName());
	    airport.put(Constants.AIRPORT_CODE, flight.getAirport().getAirportCode());
	    airport.put(Constants.LOCATION, flight.getAirport().getLocation());
	    airport.put(Constants.IS_ENABLED, flight.getAirport().isEnabled());
	    airport.put(Constants.CREATED_DATE, flight.getAirport().getCreateDateTime());
	    airport.put(Constants.UPDATED_DATE, flight.getAirport().getUpdateDateTime());
	    
	    map.put(Constants.AIRPORT, airport);
		return map;
	}
	
	private List<FlightManagerAndSFlightMappings> listOfFlightOperationManagerAndFlightMapping(String flightManagerId) {
		List<FlightManagerAndSFlightMappings> list = flightManagerAndFlightMappingRepository
				.findByFlightManagerId(flightManagerId);
		return list;
	}

	/**
	 * get flight by flight id
	 */
	public Flight getFlightById(String flightManagerId, String flightId, String realm) {
		Optional<Flight> flight = flightRepository.findById(flightId);
		if (!flight.isPresent()) {
	        throw new DataUnavailable(ErrorConstants.NO_FLIGHT_FOUND);
	    }
		List<FlightManagerAndSFlightMappings> flightMappings = listOfFlightOperationManagerAndFlightMapping(flightManagerId);
		boolean isFlightLinkedWithFlightManager = flightMappings.stream()
									.anyMatch(mapping -> mapping.getFlightId().equals(flight.get().getId()));
		if(!isFlightLinkedWithFlightManager) {
			throw new UnauthorizedRequest(ErrorConstants.UNAUTHORIZED_ACCESS_TO_THIS);
		}
		return flight.get();
	}

	/**
	 * delete the flight and all the related mapping by flightId
	 */
	public boolean deleteFlightByFlightId(String flightManagerId, String flightId, String realm) {
	    Optional<Flight> flight = flightRepository.findById(flightId);
	    if (!flight.isPresent()) {
	        return false;
	    }
	    List<FlightManagerAndSFlightMappings> flightManagerAndFlightMapping = flightManagerAndFlightMappingRepository
	            .findByFlightManagerId(flightManagerId);
	    boolean isFlightManagerLinked = flightManagerAndFlightMapping.stream()
	            .anyMatch(mapping -> mapping.getFlightId().equals(flightId));
	    if (!isFlightManagerLinked) {
	        return false;
	    }
	    flightManagerAndFlightMappingRepository.deleteByFlightId(flightId);
	    flightRepository.deleteById(flightId);
	    return true;
	}

	/**
	 * update the flight by flight ID
	 */
	public Flight updateFlightByFlightID(String flightManagerId, Flight updateFlight, String flightId, String realm) {
		Optional<Flight> existingFlight = flightRepository.findById(flightId);
	    if (!existingFlight.isPresent()) {
	        throw new DataUnavailable(ErrorConstants.FLIGHT_NOT_FOUND);
	    }
	    List<FlightManagerAndSFlightMappings> flightManagerAndFlightMapping = flightManagerAndFlightMappingRepository
	            .findByFlightManagerId(flightManagerId);
	    boolean isFlightManagerLinked = flightManagerAndFlightMapping.stream()
	            .anyMatch(mapping -> mapping.getFlightId().equals(flightId));
	    if(!isFlightManagerLinked) {
	    	throw new UnauthorizedRequest(ErrorConstants.UNAUTHORIZED_ACCESS_TO_THIS);
	    }
	    Flight flight = existingFlight.get();
	    flight.setUpdateDateTime(Timestamp.valueOf(LocalDateTime.now()));
	    flight.setAirline(updateFlight.getAirline());
	    flight.setArrivalTime(updateFlight.getArrivalTime());
	    flight.setAvailableSeats(updateFlight.getAvailableSeats());
	    flight.setDepartureTime(updateFlight.getDepartureTime());
	    flight.setFlightNumber(updateFlight.getFlightNumber());
	    flight.setFromLocation(updateFlight.getFromLocation());
	    flight.setPrice(updateFlight.getPrice());
	    flight.setTotalSeat(updateFlight.getTotalSeat());
	    flight.setToLocation(updateFlight.getToLocation());
		return flightRepository.save(flight);
	}
}
