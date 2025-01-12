package com.exception.handler;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.exception.model.DataUnavailable;
import com.exception.model.Forbidden;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;
import com.exception.model.OTPGenerationException;
import com.exception.model.UnauthorizedRequest;
import com.exception.model.WebSocketTimeoutException;
import com.exception.util.Constants;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private MessageHandler messageHandler;

	/**
	 * Constructor
	 * @param messageHandler
	 */
	public GlobalExceptionHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	/**
	 * DataUnavailable Handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = DataUnavailable.class)
	ResponseEntity<Map<String, String>> dataUnavailable(DataUnavailable ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, messageHandler.getMessage.apply(ex.getMessageKey()));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	/**
	 * InvalidRequest Handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = InvalidRequest.class)
	public ResponseEntity<Map<String, String>> requestInvalid(InvalidRequest ex){
		String message = ex.getAdditionalInfo() != null
					? MessageFormat.format(messageHandler.getMessage.apply(ex.getMessageKey()), ex.getAdditionalInfo())
					: messageHandler.getMessage.apply(ex.getMessageKey());
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, message.trim());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * UnauthorizedRequest Handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = UnauthorizedRequest.class)
	public ResponseEntity<Map<String, String>> requestUnauthorized(UnauthorizedRequest ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, messageHandler.getMessage.apply(ex.getMessageKey()));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
	
	/**
	 * Forbidden Handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Forbidden.class)
	public ResponseEntity<Map<String, String>> forbidden(UnauthorizedRequest ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, messageHandler.getMessage.apply(ex.getMessageKey()));
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}
	
	/**
	 * Forbidden Handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = InternalServerError.class)
	public ResponseEntity<Map<String, String>> internalServerError(UnauthorizedRequest ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, messageHandler.getMessage.apply(ex.getMessageKey()));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
	
	/**
	 * method argument handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, String>> serverError(MethodArgumentNotValidException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, Constants.INVALID_REQUEST);
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = Constants.ERROR_MESSAGE;
			String message = error.getDefaultMessage();
			response.put(fieldName, message);
		});
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * MethodArgumentTypeMissMatch handle
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	ResponseEntity<Map<String, String>> handleMethodArgument(MethodArgumentTypeMismatchException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, "Invalid value for parameter: "+ ex.getName());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * MissingParams handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, Constants.INVALID_REQUEST);
		response.put(Constants.ERROR_MESSAGE, "Parameter "+ ex.getParameterName() + " is missing");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * messageNotReadable handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, Constants.INVALID_REQUEST);
		response.put(Constants.ERROR_MESSAGE, "Request body is missing or unreadable");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * ConstraintViolationException handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstrainViolationException(ConstraintViolationException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, Constants.INVALID_REQUEST);
		ex.getConstraintViolations().forEach(error -> {
			String fieldName = Constants.ERROR_MESSAGE;
			String message = error.getMessage();
			response.put(fieldName, message);
		});
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * WebSocketTimeoutException handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = WebSocketTimeoutException.class)
	ResponseEntity<Map<String, String>> webSocketTimeOutError(WebSocketTimeoutException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, messageHandler.getMessage.apply(ex.getMessageKey()));
		return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
	}
	
	/**
	 * OTPGenerationException handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = OTPGenerationException.class)
	ResponseEntity<Map<String, String>> OtpGenerationExceptionError(OTPGenerationException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, ex.getErrorCode());
		response.put(Constants.ERROR_MESSAGE, messageHandler.getMessage.apply(ex.getMessageKey()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	/**
	 * MaxUploadSizeExceededException handler
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = MaxUploadSizeExceededException.class)
	ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException ex){
		Map<String, String> response = new HashMap<>();
		response.put(Constants.ERROR_CODE, Constants.PAYLOAD_TO_LARGE);
		response.put(Constants.ERROR_MESSAGE, Constants.PAYLOAD_TO_LARGE_MESSAGE);
		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
	}
}
