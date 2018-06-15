package com.akaxin.site.storage.exception;

import com.akaxin.common.utils.StringHelper;

/**
 * 迁移数据库错误会抛出此异常
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-14 19:06:40
 */
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
