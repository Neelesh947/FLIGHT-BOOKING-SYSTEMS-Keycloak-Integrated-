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
import com.example.demo.Entity.FlightOperationManagerAndAirportMappings;
import com.example.demo.Utils.Constants;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.repository.AirportRepository;
import com.example.demo.repository.FlightOperationManagerAndAirportMappingRepository;
import com.example.demo.service.AirportService;
import com.exception.model.DataUnavailable;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AirportServiceImpl implements AirportService{

	@Autowired
	private AirportRepository airportRepository;
	
	@Autowired
	private FlightOperationManagerAndAirportMappingRepository flightOperationManagerAndAirportMappingRepository;

	/**
	 * create the airports
	 */
	@Override
	public Airport createAirports(Airport airport, String flightManagerId, String realm) {
		log.info("create airport is invoked : {}", airport);
		validateAirports(airport, realm);		
		airport.setId(UUID.randomUUID().toString());
		Airport savedAirport = airportRepository.save(airport);
		String airportId = savedAirport.getId();
		if(airportId == null) {
			throw new DataUnavailable(ErrorConstants.NOT_FOUND);
		}
		FlightOperationManagerAndAirportMappings mappings = createFlightOperationManagerAndAirportMapping(
				flightManagerId, airportId);
		flightOperationManagerAndAirportMappingRepository.save(mappings);
		return savedAirport;
	}
	
	private FlightOperationManagerAndAirportMappings createFlightOperationManagerAndAirportMapping(
			String flightManagerId, String airportId) {
		FlightOperationManagerAndAirportMappings mappings = new FlightOperationManagerAndAirportMappings();
		mappings.setAirportId(airportId);
		mappings.setFlightmanagerId(flightManagerId);
		mappings.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		mappings.setUpdateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		mappings.setId(UUID.randomUUID().toString());
		return mappings;
	}
	
	private boolean validateAirports(Airport airport,String realms) {
		if(airport.getAirportName() == null || airport.getAirportName().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.AIRPORT_NAME_MANDATORY);
		}
		if(airport.getAirportCode() == null || airport.getAirportCode().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.AIRPORT_CODE);
		}
		if(airport.getLocation() == null || airport.getLocation().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.AIRPORT_LOCATION);
		}
		
		Optional<Airport> findAirportByNameCodeAndLocation = airportRepository.findByAirportNameAndAirportCodeAndLocation(
				airport.getAirportName(), airport.getAirportCode(), airport.getLocation());
		if(!findAirportByNameCodeAndLocation.isEmpty()) {
			throw new InvalidRequest(ErrorConstants.AIRPORT_NAME_CODE_LOCATION_EXIST);
		}
		
		Optional<Airport> findByAirportCodeAndLocation = airportRepository.findByAirportCodeAndLocation(
				airport.getAirportCode(), airport.getLocation());
		if(!findByAirportCodeAndLocation.isEmpty()) {
			throw new InvalidRequest(ErrorConstants.AIRPORT_CODE_LOCATION_EXIST);
		}		
		return true;
	}

	/**
	 * get the list of airports
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> getTheListOfAirport(Map<String, Object> allParams, String flightManagerId, Pageable page,
			String realm) {
		log.info("Invoked the airports");
		try {
			List<Airport> airportList = airportRepository.findAll();
			List<FlightOperationManagerAndAirportMappings> linkedAirport = listOfLinkedAirportToFlightManager(flightManagerId);
			List<Airport> listOfLinkedAirports = airportList
								.stream()
								.filter(airport -> linkedAirport.stream()
										.anyMatch(mapping -> mapping.getAirportId().equals(airport.getId())))
								.collect(Collectors.toList());
			List<Airport> sortedList = listOfLinkedAirports.stream()
					.sorted(Comparator.comparing(Airport :: getCreateDateTime).reversed())
					.collect(Collectors.toList());
			if(sortedList.size() < 1) {
				throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
			}
			if(allParams.containsKey(Constants.IS_HIDDEN) &&  !allParams.containsKey(Constants.SEARCH_STRING)) {
				sortedList = allParams.get(Constants.IS_HIDDEN).equals(Constants.TRUE) ? sortedList.stream()
						.filter(Airport -> Airport.isEnabled() == Boolean.TRUE)
						.collect(Collectors.toList()) : sortedList.stream()
						.filter(Airport -> Airport.isEnabled() == Boolean.FALSE)
						.collect(Collectors.toList());
			}
			List<Map> AirportLists = sortedList.stream().map(airport -> {
				Map<String, Object> map = generateAirportMap(airport);
				return map;
			}).collect(Collectors.toList());
			int totalElements = AirportLists.size();
			int fromIndex = page.getPageNumber() * page.getPageSize();
			int toIndex = page.getPageSize() + fromIndex;
			return new PageImpl<>(page.getPageNumber() < Math.ceil((double) totalElements / (double) page.getPageSize())
					? AirportLists.subList(fromIndex, toIndex > totalElements ? totalElements : toIndex )
					: new ArrayList<>(), page, totalElements);
		}  catch(Exception e) {
			log.info("Some exception occurred", e.getMessage() , e);
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Map<String, Object> generateAirportMap(Airport airport) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.AIRPORT_CODE, airport.getAirportCode());
		map.put(Constants.AIRPORT_NAME, airport.getAirportName());
		map.put(Constants.LOCATION, airport.getLocation());
		map.put(Constants.IS_ENABLED, airport.isEnabled());
		map.put(Constants.ID, airport.getId());
		map.put(Constants.CREATED_DATE, airport.getCreateDateTime());
		map.put(Constants.UPDATED_DATE, airport.getUpdateDateTime());
		return map;
	}

	private List<FlightOperationManagerAndAirportMappings> listOfLinkedAirportToFlightManager(String flightManagerId) {
		List<FlightOperationManagerAndAirportMappings> list = flightOperationManagerAndAirportMappingRepository
									.findByFlightmanagerId(flightManagerId);
		return list;
	}

	/**
	 * Update airport status: - active or inactive
	 */
	public String updateAirportsStatus(Map<String, Object> status, String airportId, String flightManagerId, 
			String realm) {
		Airport airportBody = airportRepository.findById(airportId)
					.orElseThrow(() -> new DataUnavailable(ErrorConstants.NO_DATA_FOUND));
		boolean noStatus = (boolean)status.get(Constants.ENABLED);
		airportBody.setEnabled(noStatus);
		airportRepository.save(airportBody);
		return Constants.TRUE;
	}

	/**
	 * delete airport by Id
	 */
	public String deleteAirportById(String airportId, String flightManagerId, String realm) {
		Optional<Airport> airport = airportRepository.findById(airportId);
		if(airport.isPresent()) {
			airportRepository.deleteById(airportId);
			return Constants.TRUE;
		} else {
			throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
		}
	}

	/**
	 * update airports
	 */
	public Airport updateAirport(String airportId, Airport airport, String flightManagerId, String realm) {
		Airport airportResponse = airportRepository.findById(airportId)
				.orElseThrow(() -> new DataUnavailable(ErrorConstants.NO_DATA_FOUND)) ;
		airportResponse.setAirportName(airport.getAirportName());
		airportResponse.setAirportCode(airport.getAirportCode());
		airportResponse.setEnabled(airport.isEnabled());
		airportResponse.setUpdateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		airportResponse.setLocation(airport.getLocation());
		airportResponse.setId(airportId);		
		return airportRepository.save(airportResponse);
	}

	@Override
	public Airport getAirportById(String airportId, String flightManagerId, String realm) {
		List<FlightOperationManagerAndAirportMappings> list = listOfLinkedAirportToFlightManager(flightManagerId);
		Airport airport = airportRepository.findById(airportId)
					.orElseThrow(( )-> new DataUnavailable(ErrorConstants.NO_DATA_FOUND));
		boolean airportExistInFlightManager = list.stream()
						.anyMatch(mapping -> mapping.getAirportId().equals(airportId));
		if(airportExistInFlightManager) {
			return airport;
		}
		else {
			throw new InvalidRequest(ErrorConstants.FLIGHT_MANGER_NOT_LINKED_WITH_AIRPORT);
		}
	}
}
