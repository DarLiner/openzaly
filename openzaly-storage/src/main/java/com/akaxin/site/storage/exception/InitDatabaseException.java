package com.akaxin.site.storage.exception;

public class InitDatabaseException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -893476933526596823L;

	public InitDatabaseException(String message) {
		super(message);
	}

	public InitDatabaseException(String message, Throwable t) {
		super(message, t);
	}

}
