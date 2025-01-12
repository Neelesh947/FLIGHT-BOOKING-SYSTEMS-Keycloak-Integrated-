package com.exception.model;

import com.exception.util.Constants;

import lombok.Getter;

@Getter
public class DataUnavailable extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	
	private final String messageKey;

	/**
	 * Constructor
	 * @param errorCode
	 * @param messageKey
	 */	
	public DataUnavailable(String messageKey) {
		this.errorCode = Constants.NOT_FOUND;
		this.messageKey = messageKey;
	}
	
	
}
