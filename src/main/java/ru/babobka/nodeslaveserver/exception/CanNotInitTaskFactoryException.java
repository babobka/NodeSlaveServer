package ru.babobka.nodeslaveserver.exception;

public class CanNotInitTaskFactoryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 471658253997347795L;

	public CanNotInitTaskFactoryException() {
		super();
	}

	public CanNotInitTaskFactoryException(String message) {
		super(message);
	}

	public CanNotInitTaskFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public CanNotInitTaskFactoryException(Throwable cause) {
		super(cause);
	}
}
