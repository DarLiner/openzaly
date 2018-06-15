package com.akaxin.site.storage.exception;

import com.akaxin.common.utils.StringHelper;

public class MigrateDatabaseException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -893476933526596823L;

	public MigrateDatabaseException(String message) {
		super(message);
	}

	public MigrateDatabaseException(String message, Throwable t) {
		super(message, t);
	}

	public MigrateDatabaseException(Throwable t) {
		super(t);
	}

	public MigrateDatabaseException(String messagePattern, Object... objects) {
		this(StringHelper.format(messagePattern, objects));
	}

}
