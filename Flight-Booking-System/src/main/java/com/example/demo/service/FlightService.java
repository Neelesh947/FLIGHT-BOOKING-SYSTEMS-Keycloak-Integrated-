package com.example.demo.service;

import com.example.demo.Entity.Flight;

public interface FlightService {

	Flight createFlightService(Flight flight, String flightManagerId, String realm);

}
