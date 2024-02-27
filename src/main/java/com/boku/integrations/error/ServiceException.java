package com.boku.integrations.error;

public class ServiceException extends RuntimeException {
	public ServiceException(String message) {
		super(message);
	}
}
