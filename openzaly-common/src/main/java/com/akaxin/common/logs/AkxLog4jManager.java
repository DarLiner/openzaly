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

import java.util.Enumeration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <pre>
 * 	管理log4j相关设置
 * 		1.日志等级的动态修改
 * 		2.其他
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-26 17:03:37
 */
public class AkxLog4jManager {
	private static Logger logger = Logger.getLogger(AkxLog4jManager.class);

	public static Level getLogLevel() {
		Logger rootLogger = Logger.getRootLogger();
		return rootLogger.getLevel();
	}

	// 设置日志级别
	public static void setLogLevel(Level level) {
		try {
			Logger rootLogger = Logger.getRootLogger();
			if (rootLogger.getLevel() == level) {
				return;
			}
			rootLogger.setLevel(level);
			Enumeration<Logger> logEnum = getAllLogger();
			while (logEnum.hasMoreElements()) {
				Logger logger = logEnum.nextElement();
				logger.setLevel(level);
			}
			logger.info("set site server log_level=" + level);
		} catch (Exception e) {
			logger.error("update log4j level=" + level + " error", e);
		}
		logger.info("update log4j level=" + level + " finish");
	}

	// 获取所有的日志logger
	public static Enumeration<Logger> getAllLogger() {
		Logger rootLogger = Logger.getRootLogger();
		@SuppressWarnings("unchecked")
		Enumeration<Logger> logEnum = rootLogger.getLoggerRepository().getCurrentLoggers();
		return logEnum;
	}

}
