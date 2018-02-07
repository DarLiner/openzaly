/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.common.logs;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:31:53
 */
public class LogCreater {
	private static final Logger logger = Logger.getLogger(LogCreater.class);
	private static final String DEFAULT_SITE_PATH = "site-logs/";

	public static Logger createLogger(String logName) {
		return createLogger(logName, null, new PatternLayout(), false, false);
	}

	public static Logger createLogger(String logName, String logPath) {
		return createLogger(logName, logPath, new PatternLayout(), false, false);
	}

	public static Logger createTimeLogger(String logName) {
		return createLogger(logName, null, new PatternLayout("[%p] %d [%c] \r\n\t%m%n"), false, false);
	}

	public static Logger createTimeLogger(String logName, String logPath) {
		return createLogger(logName, null, new PatternLayout("[%p] %d [%c] \r\n\t%m%n"), false, false);
	}

	/**
	 * 
	 * @param logName
	 *            log文件名称
	 * @param logPath
	 *            log路径
	 * @param layout
	 *            log布局
	 * @param additivity
	 *            子类appender是否集成父类appender
	 * @param isDailyMode
	 *            是否按天来记录
	 * @return
	 */
	public static Logger createLogger(String logName, String logPath, PatternLayout layout, boolean additivity,
			boolean isDailyMode) {
		Logger createdLogger = Logger.getLogger(logName);
		try {
			String logFileName = null;
			if (StringUtils.isEmpty(logPath)) {
				logFileName = DEFAULT_SITE_PATH + logName + ".log";
			} else if (logPath.endsWith("/")) {
				logFileName = logPath + logName + ".log";
			} else {
				logFileName = logPath + "/" + logName + ".log";
			}

			FileAppender fileAppender = null;
			if (isDailyMode) {
				fileAppender = new DailyRollingFileAppender(layout, logFileName, "'.'yyyy-MM-dd");

			} else {
				fileAppender = new RollingFileAppender(layout, logFileName, true);
				RollingFileAppender rollingAppender = (RollingFileAppender) fileAppender;
				rollingAppender.setMaxFileSize("200MB");
				rollingAppender.setMaxBackupIndex(5);
			}
			// not need bufferedIO
			// fileAppender.setBufferedIO(true);
			// fileAppender.setBufferSize(8192);

			createdLogger = Logger.getLogger(logName);
			createdLogger.removeAllAppenders();
			createdLogger.setAdditivity(additivity);
			createdLogger.addAppender(fileAppender);
		} catch (Exception e) {
			logger.error("create logger error", e);
			throw new RuntimeException();
		}
		return createdLogger;
	}
}
