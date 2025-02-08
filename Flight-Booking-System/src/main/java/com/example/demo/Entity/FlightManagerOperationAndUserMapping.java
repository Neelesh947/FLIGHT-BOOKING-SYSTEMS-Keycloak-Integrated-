package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Setter
@Getter
@NoArgsConstructor
public class FlightManagerOperationAndUserMapping extends BaseEntity{

	@Column(nullable = false)
	private String userId;
	
	@Column(nullable = false)
	private String flightmanagerId;
}
