package com.example.demo.service;

import org.springframework.http.ResponseEntity;

import com.example.vo.FlightOperationManager;

public interface FlightOperationManagerService {

	ResponseEntity<Object> createFlightOperationmanager(String adminId, FlightOperationManager flightOperationManager,
			String realm);

}
