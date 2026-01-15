package com.ddlab.rnd.exception;

public class BadCredentialsException extends RuntimeException {
	
	private static final long serialVersionUID = 6633758524311629042L;

	public BadCredentialsException(String message) {
		super(message);
	}

}
