package com.exception.model;

import com.exception.util.Constants;

import lombok.Getter;

@Getter
public class Forbidden extends RuntimeException {

private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	
	private final String messageKey;

	/**
	 * Constructor
	 * @param errorCode
	 * @param messageKey
	 */	
	public Forbidden(String messageKey) {
		this.errorCode = Constants.ACCESS_DENIED;
		this.messageKey = messageKey;
	}
}
