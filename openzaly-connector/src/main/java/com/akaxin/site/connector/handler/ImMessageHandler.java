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
package com.akaxin.site.connector.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.site.message.service.ImMessageService;

/**
 * 这里负责下发消息至message模块进行处理
 * 
 * @author sam
 *
 * @param <Command>
 */
public class ImMessageHandler extends AbstractCommonHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImMessageHandler.class);

	public boolean handle(Command command) {
		try {
			ChannelSession channelSession = command.getChannelSession();
			String deviceId = channelSession.getDeviceId();
			if (StringUtils.isEmpty(deviceId)) {
				logger.info("im request fail.deviceId ={}.", deviceId);
				return false;
			}
			ChannelSession acsession = ChannelManager.getChannelSession(deviceId);
			if (acsession == null) {
				logger.info("im request fail.authedChannelSession={}", acsession);
				return false;
			}
			if (!checkSiteUserId(command.getSiteUserId(), acsession.getUserId())) {
				logger.info("im request fail.cmdUserId={},sessionUserId={}", command.getSiteUserId(),
						acsession.getUserId());
				return false;
			}

			return new ImMessageService().execute(command);
		} catch (Exception e) {
			logger.error("im request error.", e);
		}
		return false;
	}

	/**
	 * 比较
	 * 
	 * @param cmdUserId
	 * @param sessionUserId
	 * @return
	 */
	private boolean checkSiteUserId(String cmdUserId, String sessionUserId) {
		return StringUtils.isBlank(sessionUserId) ? false : sessionUserId.equals(cmdUserId);
	}

}
