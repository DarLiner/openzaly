package com.akaxin.site.connector.exception;

public class TcpServerException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7366967641018300790L;

	public TcpServerException(String message) {
		super(message);
	}

	public TcpServerException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
