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
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.utils.StringHelper;

/**
 * 封装log，针对网络日志以及数据库操作日志
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 16:13:00
 */
public class LogUtils extends LogCreater {

	public static void requestInfoLog(Logger logger, Command command, String messagePattern, Object... objects) {
		FormattingTuple format = MessageFormatter.arrayFormat(messagePattern, objects);
		logger.info("client={} siteUserId={} action={} msg={}", command.getClientIp(), command.getSiteUserId(),
				command.getAction(), command.toString(), format.getMessage());
		return;
	}

	public static void requestDebugLog(Logger logger, Command command, String requestStr) {
		logger.debug("client={} siteUserId={} action={} command={} request={}", command.getClientIp(),
				command.getSiteUserId(), command.getAction(), command.toString(), requestStr);
	}

	public static void requestErrorLog(Logger logger, Command command, Throwable t) {
		logger.error(StringHelper.format("client={} siteUserId={} action={} uri={} error", command.getClientIp(),
				command.getSiteUserId(), command.getAction(), command.getUri()), t);
	}

	public static void requestErrorLog(Logger logger, Command command, Class<?> clazz, Throwable t) {
		logger.error(StringHelper.format("client={} siteUserId={} action={} uri={} {} error", command.getClientIp(),
				command.getSiteUserId(), command.getAction(), command.getUri(), clazz.getClass().getName()), t);
	}

	public static void requestResultLog(Logger logger, Command command, CommandResponse response) {
		try {
			logger.info("client={} siteUserId={} action={} uri={} cost={}ms result={}", command.getClientIp(),
					command.getSiteUserId(), command.getAction(), command.getUri(),
					System.currentTimeMillis() - command.getStartTime(), response.getErrorCodeInfo());
		} catch (Exception e) {
			logger.error(StringHelper.format("request result log error command={} response={}", command, response), e);
		}
	}

	public static void dbDebugLog(Logger logger, long startTime, Object result, String sql, Object... objects) {
		String messagePattern = sql.replace("?", "{}");
		FormattingTuple format = MessageFormatter.arrayFormat(messagePattern, objects);
		logger.debug("[openzaly-db] cost:{}ms result:{} sql:{}", System.currentTimeMillis() - startTime, result,
				format.getMessage());
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
