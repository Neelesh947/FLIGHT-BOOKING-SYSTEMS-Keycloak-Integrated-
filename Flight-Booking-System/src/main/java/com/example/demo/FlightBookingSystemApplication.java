package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.example.demo.config","com.keycloak","com.keycloak.service",
		"com.exception.handler","com.example.utils","com.example.vo","com.example"})
@ComponentScan(basePackages = {"com.example.demo"})
public class FlightBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightBookingSystemApplication.class, args);
	}

}
