package ru.babobka.nodeslaveserver.exception;

public class ServerConfigurationException  extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3603894271678084823L;

	public ServerConfigurationException() {
		super();
	}

	public ServerConfigurationException(String message) {
		super(message);
	}

	public ServerConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerConfigurationException(Throwable cause) {
		super(cause);
	}
}
