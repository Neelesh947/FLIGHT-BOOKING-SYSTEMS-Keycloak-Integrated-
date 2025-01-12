package com.exception.model;

import com.exception.util.Constants;

import lombok.Getter;

@Getter
public class WebSocketTimeoutException extends RuntimeException{

private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	
	private final String messageKey;

	/**
	 * Constructor
	 * @param errorCode
	 * @param messageKey
	 */	
	public WebSocketTimeoutException(String errorCode, String messageKey) {
		this.errorCode = Constants.INTERNAL_ERROR;
		this.messageKey = messageKey;
	}
}
