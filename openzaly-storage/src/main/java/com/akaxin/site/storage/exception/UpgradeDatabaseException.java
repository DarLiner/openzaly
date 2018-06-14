package com.akaxin.site.storage.exception;

import com.akaxin.common.utils.StringHelper;

public class UpgradeDatabaseException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -893476933526596823L;

	public UpgradeDatabaseException(String message) {
		super(message);
	}

	public UpgradeDatabaseException(String message, Throwable t) {
		super(message, t);
	}

	public UpgradeDatabaseException(String messagePattern, Object... objects) {
		this(StringHelper.format(messagePattern, objects));
	}

}
