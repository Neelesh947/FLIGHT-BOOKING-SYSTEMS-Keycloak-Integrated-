package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Flight;
import com.example.demo.Utils.Constants;
import com.example.demo.Utils.SecurityUtils;
import com.example.demo.service.FlightService;

@RestController
@RequestMapping("/{realm}/Flight")
public class FlightController {

	@Autowired
	private FlightService flightService;
	
	/**
	 * Create flight
	 * @param flight
	 * @param realm
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Flight> createFlight(@RequestBody Flight flight, @PathVariable String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Flight createdFlight = flightService.createFlightService(flight, flightManagerId, realm);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdFlight);
	}
	
	/**
	 * get list of all flight that associated with the flight manager id
	 * @param allParams
	 * @param pageable
	 * @param realm
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping
	public ResponseEntity<Page<Map>> getFlightList(@RequestParam Map<String, Object> allParams,
			Pageable pageable, @PathVariable String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Page<Map> flight = flightService.getListOfFlight(flightManagerId, allParams, pageable, realm);
		return ResponseEntity.ok(flight);
	}
	
	/**
	 * get the flight by flight id
	 * @param flightId
	 * @param realm
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Flight> getFlightByFlightId(@PathVariable("id") String flightId, String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Flight flight = flightService.getFlightById(flightManagerId, flightId, realm);
		return ResponseEntity.ok(flight);
	}
	
	 /**
     * Delete a flight by its ID and also remove the flight manager's mapping.
     * 
     * @param flightManagerId - The ID of the flight manager attempting to delete the flight
     * @param flightId        - The ID of the flight to be deleted
     * @return ResponseEntity with status indicating success or failure
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteFlightByFlightId(@PathVariable("id") String flightId, String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		boolean isFlightDeleted = flightService.deleteFlightByFlightId(flightManagerId, flightId, realm);
		if(isFlightDeleted) {
			return ResponseEntity.ok(Constants.FLIGHT_DELETED_SUCCESSFULLY);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong...");
		}
	}
	
	/**
	 * Update the flight by its ID
	 * @param flightId - The ID of the flight to be updated
	 * @param realm
	 * @return ResponseEntity with status indicating success or failure
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Flight> updateFlightByFlightId(@PathVariable("id") String flightId,
			@RequestBody Flight updateFlight , String realm){
		String flightManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Flight updatedFlight = flightService.updateFlightByFlightID(flightManagerId, updateFlight, flightId, realm);
		return ResponseEntity.status(HttpStatus.OK).body(updatedFlight);
	}
}
