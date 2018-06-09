package com.akaxin.site.storage.exception;

public class NeedInitMysqlException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -893476933526596823L;

	public NeedInitMysqlException(String message) {
		super(message);
	}

	public NeedInitMysqlException(String message, Throwable t) {
		super(message, t);
	}

}
