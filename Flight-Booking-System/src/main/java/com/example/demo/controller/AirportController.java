package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Airport;
import com.example.demo.Utils.SecurityUtils;
import com.example.demo.config.Constants;
import com.example.demo.service.AirportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{realm}/airport")
public class AirportController {

	@Autowired
	private AirportService airportService;
	
	/**
	 * Create airport and the role must be the flightOperationMangaer
	 * @param airport
	 * @param realm
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Airport> createAirports(@RequestBody Airport airport, @PathVariable String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Airport createdAirport  = airportService.createAirports(airport, flightManagerId, realm);		
		return ResponseEntity.status(HttpStatus.CREATED).body(createdAirport);
	}
	
	/**
	 *  Get the list of airports
	 * @param allParams
	 * @param pageable
	 * @param realm
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping
	public ResponseEntity<?> getTheListOfAirports(@RequestParam Map<String, Object> allParams,
			Pageable pageable, @PathVariable String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Page<Map> airports = airportService.getTheListOfAirport(allParams, flightManagerId, pageable, realm);
		return ResponseEntity.ok(airports);
	}
	
	@PatchMapping("/updateStatus/{id}")
	public ResponseEntity<?> updateAirportStatus(@RequestBody Map<String, Object> status, 
			@PathVariable("id") String airportId, @PathVariable String realm){
		log.info("Invoked update airport status");
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		String response = airportService.updateAirportsStatus(status, airportId, flightManagerId, realm);
		Map<String, Object> resMap = new HashMap<>();
		if(response.equals(Constants.TRUE)) {
			resMap.put(Constants.STATUS, Constants.SUCCESS);
			resMap.put(Constants.MESSAGE, Constants.SUCCESSFULLY_UPDATED);
			return new ResponseEntity<>(resMap, HttpStatus.OK);
		}
		return null;
	}
}
