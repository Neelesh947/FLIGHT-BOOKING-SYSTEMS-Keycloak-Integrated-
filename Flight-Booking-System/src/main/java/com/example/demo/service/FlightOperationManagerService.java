package com.example.demo.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.example.demo.Entity.FlightOperationManager;

public interface FlightOperationManagerService {

	ResponseEntity<Object> createFlightOperationmanager(String adminId, FlightOperationManager flightOperationManager,
			String realm);

	@SuppressWarnings("rawtypes")
	Page<Map> fetchOperationManager(Map<String, Object> allParams, String adminId, Pageable pageable, String realm);

	String updateFlightOperationManagerStatus(Map<String, Object> status, String fomId, String realm);
}
