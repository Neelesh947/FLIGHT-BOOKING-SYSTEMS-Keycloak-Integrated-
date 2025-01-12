package com.exception.model;

import com.exception.util.Constants;

import lombok.Getter;

@Getter
public class InvalidRequest extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	
	private final String messageKey;
	
	private final String additionalInfo;

	/**
	 * Constructor
	 * @param errorCode
	 * @param messageKey
	 */	
	public InvalidRequest(String messageKey) {
		this.errorCode = Constants.INVALID_REQUEST;
		this.messageKey = messageKey;
		this.additionalInfo = null;
	}
	
	public InvalidRequest(String messageKey, String messageValue) {
		this.errorCode = Constants.INVALID_REQUEST;
		this.messageKey = messageKey;
		this.additionalInfo = messageValue;
	}
}
