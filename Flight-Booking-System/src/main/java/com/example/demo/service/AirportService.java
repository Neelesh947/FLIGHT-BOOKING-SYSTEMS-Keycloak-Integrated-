package com.example.demo.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.Entity.Airport;

public interface AirportService {

	Airport createAirports(Airport airport, String flightManagerId, String realm);

	@SuppressWarnings("rawtypes")
	Page<Map> getTheListOfAirport(Map<String, Object> allParams, String flightManagerId, Pageable pageable,
			String realm);

	String updateAirportsStatus(Map<String, Object> status, String airportId, String flightManagerId, String realm);

	String deleteAirportById(String airportId, String flightManagerId, String realm);

	Airport updateAirport(String airportId, Airport airport, String flightManagerId, String realm);

	Airport getAirportById(String airportId, String flightManagerId, String realm);

}
