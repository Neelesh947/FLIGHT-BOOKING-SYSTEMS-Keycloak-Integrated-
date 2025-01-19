package com.example.demo.schedular;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.repository.FlightRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlightSchedular {

	@Autowired
	private FlightRepository flightRepository;
	
//	@Transactional
//	@Scheduled(fixedRate = 60000)
	public void deleteDeparturedFlight() {
		log.info("Cron job started");
		LocalDateTime time=LocalDateTime.now();
		flightRepository.deleteByDepartureTimeBefore(time);
	}
	
}
