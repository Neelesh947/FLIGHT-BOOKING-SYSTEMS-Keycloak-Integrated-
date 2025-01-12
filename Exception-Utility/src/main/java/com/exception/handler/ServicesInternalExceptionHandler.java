package com.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.exception.model.DataUnavailable;
import com.exception.model.Forbidden;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;
import com.exception.model.TerminalUnreachableException;
import com.exception.model.UnauthorizedRequest;
import com.exception.util.Constants;
import com.exception.util.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServicesInternalExceptionHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public RuntimeException handleException(Exception ex) {
		if(ex instanceof HttpClientErrorException clientErrorException) {
			return handleHttpClientError(clientErrorException);
		} else if(ex instanceof HttpServerErrorException serverError) {
			return handleHttpServerError(serverError);
		} else {
			//Handle non-Http exception
			log.error("unexpected non-Http error occured: ", ex);
			throw new InternalServerError(Constants.INTERNAL_EXCEPTION);
		}
	}
	
	private RuntimeException handleHttpClientError(HttpClientErrorException ex) {
		log.info("handleHttpCleintError method invoked");
		HttpStatus status = (HttpStatus) ex.getStatusCode();
		String errorMessage = processErrorResponse(ex);
		
		return switch (status) {
		case BAD_REQUEST -> new InvalidRequest(errorMessage);
		case UNAUTHORIZED -> new UnauthorizedRequest(errorMessage);
		case FORBIDDEN -> new Forbidden(errorMessage);
		case NOT_FOUND -> new DataUnavailable(errorMessage);
		default -> handleUnexpectedClientError(status ,ex);
	};
	}
	
	private RuntimeException handleHttpServerError(HttpServerErrorException ex) {
		HttpStatus status = (HttpStatus) ex.getStatusCode();
		String errorMessage = processErrorResponse(ex);
		
		return switch (status) {
			case SERVICE_UNAVAILABLE -> new TerminalUnreachableException(errorMessage);
			default -> handleUnexpectedServerError(status, ex);
		};
	}
	
	private String processErrorResponse(Exception ex) {
		if(ex instanceof HttpClientErrorException || ex instanceof HttpServerErrorException) {
			String responseBody = ((HttpClientErrorException) ex).getResponseBodyAsString();
			if(responseBody == null || responseBody.isEmpty()) {
				log.warn("Response body is null or empty for status: {}", ((HttpClientErrorException) ex).getStatusCode());
				return Constants.INTERNAL_EXCEPTION;
			}
			try {
				ErrorResponse errorResponse = OBJECT_MAPPER.readValue(responseBody, ErrorResponse.class);
				return errorResponse.getErrorMessage();
			} catch (JsonProcessingException e) {
				log.error("Error parsing error response body", e);
				throw new InternalServerError(Constants.INTERNAL_EXCEPTION);
			}	
		}
		return Constants.INTERNAL_EXCEPTION;
	}
	
	private RuntimeException handleUnexpectedClientError(HttpStatus status, HttpClientErrorException ex) {
		log.error("Client error occurred: {}, response body: {}",status, ex.getResponseBodyAsString(), ex);
		throw new InternalServerError(Constants.INTERNAL_EXCEPTION);
	}
	
	private RuntimeException handleUnexpectedServerError(HttpStatus status, HttpServerErrorException ex) {
		log.error("Server error occurred: {}, response body: {}",status, ex.getResponseBodyAsString(), ex);
		throw new InternalServerError(Constants.INTERNAL_EXCEPTION);
	}
}
