package com.akaxin.site.connector.exception;

/**
 * define custom exception for httpserver
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-19 11:50:13
 */
public class HttpServerException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6242277890587744071L;

	public HttpServerException(String message) {
		super(message);
	}

	public HttpServerException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
