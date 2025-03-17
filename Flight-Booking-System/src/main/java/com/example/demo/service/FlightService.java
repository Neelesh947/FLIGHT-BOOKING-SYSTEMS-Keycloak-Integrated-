package com.example.demo.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.Entity.Flight;

public interface FlightService {

	Flight createFlightService(Flight flight, String flightManagerId, String realm);

	Page<Map> getListOfFlight(String flightManagerId, Map<String, Object> allParams, Pageable pageable, String realm);

	Flight getFlightById(String flightManagerId, String flightId, String realm);

	boolean deleteFlightByFlightId(String flightManagerId, String flightId, String realm);

	Flight updateFlightByFlightID(String flightManagerId, Flight updateFlight,String flightId, String realm);

}
