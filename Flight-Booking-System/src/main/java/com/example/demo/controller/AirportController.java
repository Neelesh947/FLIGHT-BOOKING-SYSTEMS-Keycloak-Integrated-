package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	/**
	 * Update the status of airport: - active and inactive
	 * @param status
	 * @param airportId
	 * @param realm
	 * @return
	 */
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
	
	/**
	 * Delete the airport based on airportId
	 * @param airportId
	 * @param realm
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAirport(@PathVariable("id") String airportId, @PathVariable String realm){
		log.info("delete for airport with id: {}", airportId);
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		String response = airportService.deleteAirportById(airportId, flightManagerId, realm);
		Map<String, Object> resMap = new HashMap<>();
		if(response.equals(Constants.TRUE)) {
			resMap.put(Constants.STATUS, Constants.SUCCESS);
			resMap.put(Constants.MESSAGE, Constants.SUCCESSFULLY_UPDATED);
			return new ResponseEntity<>(resMap, HttpStatus.OK);
		}
		return null;
	}
	
	/**
	 * update the airport by using the airportId but in body everything.
	 * @param airportId
	 * @param airport
	 * @param realm
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Airport> updateAirport(@PathVariable("id") String airportId, @RequestBody Airport airport,
				@PathVariable String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Airport airportResponse = airportService.updateAirport(airportId, airport, flightManagerId, realm);
		return ResponseEntity.status(HttpStatus.OK).body(airportResponse);
	}
	
	/**
	 * get the airports by airportId
	 * @param airportId
	 * @param realm
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Airport> getAirportById(@PathVariable("id") String airportId, String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Airport response = airportService.getAirportById(airportId, flightManagerId, realm);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
