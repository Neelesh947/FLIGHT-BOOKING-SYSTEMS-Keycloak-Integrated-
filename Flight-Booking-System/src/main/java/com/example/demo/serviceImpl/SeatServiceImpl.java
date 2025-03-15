package com.example.demo.serviceImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Flight;
import com.example.demo.Entity.Seat;
import com.example.demo.Enum.SeatClass;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.Seatrepository;
import com.example.demo.service.SeatService;
import com.exception.model.DataUnavailable;
import com.exception.model.InvalidRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeatServiceImpl implements SeatService{

	@Autowired
	private Seatrepository seatRepository;
	
	@Autowired
	private FlightRepository flightRepository;
	
	private static final int MAX_ROWS = 30;
	private static final char[] SEAT_COLUMNS = {'A', 'B', 'C', 'D', 'E', 'F'};

	@Override
	public Seat createSeat(Seat seat, String flightOperationManagerId, String realm) {
		log.info("seat creation process invoked : {}", seat);
		validateSeat(seat);
		seat.setId(UUID.randomUUID().toString());
		String seatNumber = generateSeatNumber(seat.getSeatClass(), seat.getSeatNumber());
		seatNumber = findaAvailableSeat(seat.getSeatClass(), seatNumber);
		seat.setSeatNumber(seatNumber);
		Flight flight = flightRepository.findById(seat.getFlight().getId())
							.orElseThrow(() -> new DataUnavailable(ErrorConstants.FLIGHT_NOT_FOUND));
		seat.setFlight(flight);
		Seat seatRep = seatRepository.save(seat);
		return seatRep;
	}
	
	private String findaAvailableSeat(SeatClass seatClass, String seatNumber) {
		String classPfrefix = getPrefixClass(seatClass);
		int rowNumber = Integer.parseInt(seatNumber.replaceAll("[^0-9]", ""));  
        char seatLetter = seatNumber.charAt(seatNumber.length() - 1);
        
        while(true) {
        	Optional<Seat> existingSeat = seatRepository.findBySeatNumber(classPfrefix + "-" + rowNumber + seatLetter);
            if (!existingSeat.isPresent()) {
            	return classPfrefix + "-" + rowNumber + seatLetter;
            }
            seatLetter = getNextSeatLetter(seatLetter);
            if (seatLetter > 'F') {
                seatLetter = 'A';
                rowNumber++;
                if (rowNumber > MAX_ROWS) {
                    throw new InvalidRequest(ErrorConstants.SEAT_ROW_LIMIT_REACHED);
                }
            }
        }
	}
	
	private char getNextSeatLetter(char currentLetter) {
		if (currentLetter >= 'A' && currentLetter <= 'F') {
            return (char) (currentLetter + 1);
        }
		return currentLetter;
	}
		
	private boolean validateSeat(Seat seat) {
		Optional<Seat> seatResult = seatRepository.findBySeatNumber(seat.getSeatNumber());
		if(seatResult.isPresent()) {
			throw new InvalidRequest(ErrorConstants.SEAT_NUMBER_ALREADY_PRESENT);
		}
		return true;
	}
	
	private String generateSeatNumber(SeatClass seatClass, String rowNumber) {
		int row = Integer.parseInt(rowNumber);
		String seatNumber = getSeatLetterForRow(row);
		String classPfrefix = getPrefixClass(seatClass);
		return classPfrefix + "-" + rowNumber + seatNumber;
	}
	
	private String getSeatLetterForRow(int rowNumber) {
		return String.valueOf(SEAT_COLUMNS[(rowNumber - 1) % SEAT_COLUMNS.length]);
	}
	
	private String getPrefixClass(SeatClass seatClass) {
		switch (seatClass) {
		case ECONOMY:
            return "Economy";
        case BUSINESS:
            return "Business";
        case FIRST_CLASS:
            return "First";
        default:
            throw new InvalidRequest(ErrorConstants.UNSUPPORTED_SEAT_CLASS);
		}
	}

	/**
	 * get list of seats 
	 */
	public List<Seat> getListOfSeats(Map<String, Object> allParams, Pageable pageable, String flightOperationManagerId,
			String realm) {
		List<Seat> seatList = seatRepository.findAll();
		List<Seat> listOfSortedSeat = seatList.stream()
						.sorted(Comparator.comparing(Seat :: getCreateDateTime).reversed())
						.collect(Collectors.toList());
		
		if(listOfSortedSeat.size() < 1) {
			throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
		}
		return listOfSortedSeat;
	}
}
