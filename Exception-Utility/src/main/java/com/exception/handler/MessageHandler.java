package com.exception.handler;

import java.util.Optional;
import java.util.function.UnaryOperator;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {

	private MessageSource messageSource;

	public MessageHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public final UnaryOperator<String> getMessage = code -> {
		try {
			return Optional.ofNullable(messageSource.getMessage(code, null, null)).orElse(code);
		} catch (NoSuchMessageException e) {
		   return code;	
		}
	}; 
}
