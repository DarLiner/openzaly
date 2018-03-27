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

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import com.akaxin.common.command.Command;
import com.akaxin.common.utils.StringHelper;

/**
 * 封装log，针对网络日志以及数据库操作日志
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 16:13:00
 */
public class LogUtils extends LogCreater {

	public static void apiRequestLog(Logger logger, Command command, String requestStr) {
		logger.debug("client={} siteUserId={} action={} command={} request={}", command.getClientIp(),
				command.getSiteUserId(), command.getAction(), requestStr);
	}

	public static void apiErrorLog(Logger logger, Command command, Throwable t) {
		logger.error(StringHelper.format("client={} siteUserId={} action={} error", command.getClientIp(),
				command.getSiteUserId(), command.getAction()), t);
	}

	public static void apiResultLog(Logger logger, Command command, String errCode) {
		logger.debug("client={} siteUserId={} action={} result={}", command.getClientIp(), command.getSiteUserId(),
				command.getAction(), errCode);
	}

	public static void printDBLog(Logger logger, long cost, Object result, String sql) {
		logger.info("DB -> costTime:{} result:{} sql:{}", cost, result, sql);
	}

	public static void info(org.apache.log4j.Logger logger, String messagePattern, Object object) {
		FormattingTuple format = MessageFormatter.format(messagePattern, object);
		logger.info(format.getMessage());
	}

	public static void info(org.apache.log4j.Logger logger, String messagePattern, Object... objects) {
		FormattingTuple format = MessageFormatter.arrayFormat(messagePattern, objects);
		logger.info(format.getMessage());
	}
}
