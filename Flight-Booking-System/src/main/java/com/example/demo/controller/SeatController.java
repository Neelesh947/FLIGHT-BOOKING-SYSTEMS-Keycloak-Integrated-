package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Seat;
import com.example.demo.Utils.SecurityUtils;
import com.example.demo.service.SeatService;

@RestController
@RequestMapping("/{realm}/seat")
public class SeatController {

	@Autowired
	private SeatService seatService;
	
	/**
	 * Create Seat
	 * @param seat
	 * @param realm
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Seat> createSeat(@RequestBody Seat seat, @PathVariable String realm){
		String flightOperationManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		Seat createdSeat = seatService.createSeat(seat, flightOperationManagerId, realm);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdSeat);
	}
	
	/**
	 * Get list of seats
	 * @param allParams
	 * @param pageable
	 * @param realm
	 * @return
	 */
	@GetMapping
	public ResponseEntity<List<Seat>> getListOfSeats(@RequestParam Map<String, Object> allParams,
			Pageable pageable, @PathVariable String realm){
		String flightOperationManagerId = SecurityUtils.getCurrentUserIdSupplier.get();
		List<Seat> seatList = seatService.getListOfSeats(allParams, pageable, flightOperationManagerId, realm);
		return ResponseEntity.status(HttpStatus.OK).body(seatList);
	}
}
