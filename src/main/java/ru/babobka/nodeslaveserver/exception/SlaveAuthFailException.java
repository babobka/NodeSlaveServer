package ru.babobka.nodeslaveserver.exception;

import java.io.IOException;

public class SlaveAuthFailException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3603894271678084823L;

	public SlaveAuthFailException() {
		super();
	}

	public SlaveAuthFailException(String message) {
		super(message);
	}

	public SlaveAuthFailException(String message, Throwable cause) {
		super(message, cause);
	}

	public SlaveAuthFailException(Throwable cause) {
		super(cause);
	}
}
