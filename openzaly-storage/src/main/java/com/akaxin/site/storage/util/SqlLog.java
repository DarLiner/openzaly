package com.akaxin.site.storage.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlLog {
	private static final Logger logger = LoggerFactory.getLogger(SqlLog.class);

	public static void into(String msg) {
		logger.info(msg);
	}

	public static void into(String messagePattern, Object... objs) {
		logger.info(messagePattern, objs);
	}

	public static void error(String msg) {
		logger.error(msg);
	}

	public static void error(String msg, Throwable t) {
		logger.error(msg, t);
	}
}
