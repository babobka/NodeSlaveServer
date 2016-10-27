package ru.babobka.nodeslaveserver.exception;

public class ClientConfigurationException  extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3603894271678084823L;

	public ClientConfigurationException() {
		super();
	}

	public ClientConfigurationException(String message) {
		super(message);
	}

	public ClientConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientConfigurationException(Throwable cause) {
		super(cause);
	}
}
