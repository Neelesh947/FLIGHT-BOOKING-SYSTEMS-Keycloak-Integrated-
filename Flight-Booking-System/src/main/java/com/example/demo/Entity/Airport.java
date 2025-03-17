package com.example.demo.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport extends BaseEntity{
	
	private String airportName;
	
	private String airportCode;
	
	private String location;
	
	@OneToMany(mappedBy = "airport", fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Flight> flight;
	
	private boolean isEnabled;
}
