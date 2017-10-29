package com.adeptions.jagol.cli;

public class BadCommandException extends RuntimeException {
	public BadCommandException(String message) {
		super(message);
	}
}
