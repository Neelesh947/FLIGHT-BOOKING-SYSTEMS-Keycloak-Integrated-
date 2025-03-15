package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Flight;
import com.example.demo.Utils.SecurityUtils;
import com.example.demo.service.FlightService;

@RestController
@RequestMapping("/{realm}/Flight")
public class FlightController {

	@Autowired
	private FlightService flightService;
	
	@PostMapping
	public ResponseEntity<Flight> createFlight(@RequestBody Flight flight, @PathVariable String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Flight createdFlight = flightService.createFlightService(flight, flightManagerId, realm);
		return null;
	}
}
