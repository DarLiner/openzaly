package com.akaxin.site.boot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootLog {
	private static final Logger logger = LoggerFactory.getLogger(BootLog.class);

	public static void info(String message) {
		logger.info(message);
	}

	public static void info(String message, Object... objs) {
		logger.info(message, objs);
	}

	public static void error(String message, Throwable t) {
		logger.error(message, t);
	}

	public static void error(String message) {
		logger.error(message);
	}
}
