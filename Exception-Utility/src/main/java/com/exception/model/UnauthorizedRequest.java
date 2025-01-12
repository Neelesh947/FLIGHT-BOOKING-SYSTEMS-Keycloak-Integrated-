package com.exception.model;

import com.exception.util.Constants;

import lombok.Getter;

@Getter
public class UnauthorizedRequest extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	
	private final String messageKey;

	/**
	 * Constructor
	 * @param errorCode
	 * @param messageKey
	 */	
	public UnauthorizedRequest(String messageKey) {
		this.errorCode = Constants.UNAUTHORIZED_REQUEST;
		this.messageKey = messageKey;
	}
}
