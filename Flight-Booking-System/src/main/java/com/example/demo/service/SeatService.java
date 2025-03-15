package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.example.demo.Entity.Seat;

public interface SeatService {

	Seat createSeat(Seat seat, String flightOperationManagerId, String realm);

	List<Seat> getListOfSeats(Map<String, Object> allParams, Pageable pageable, String flightOperationManagerId,
			String realm);

}
