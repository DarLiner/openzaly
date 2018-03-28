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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.message.service.ImMessageService;

/**
 * 这里负责下发消息至message模块进行处理
 * 
 * @author sam
 *
 * @param <Command>
 */
public class ImMessageHandler extends AbstractCommonHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ImMessageHandler.class);

	public CommandResponse handle(Command command) {
		try {
			ChannelSession channelSession = command.getChannelSession();
			String deviceId = channelSession.getDeviceId();
			if (StringUtils.isEmpty(deviceId)) {
				channelSession.getChannel().close();
				logger.error("{} client={} im request error with deviceId={}.", AkxProject.PLN, command.getClientIp(),
						deviceId);
				return null;
			}

			ChannelSession acsession = ChannelManager.getChannelSession(deviceId);
			if (acsession == null) {
				channelSession.getChannel().close();
				logger.info("{} client={} im request error with channelSession={}", acsession);
				return null;
			}

			if (!checkSiteUserId(command.getSiteUserId(), acsession.getUserId())) {
				channelSession.getChannel().close();
				logger.info("im request fail.cmdUserId={},sessionUserId={}", command.getSiteUserId(),
						acsession.getUserId());
				return null;
			}

			if (RequestAction.IM_CTS_PING.getName().equalsIgnoreCase(command.getAction())) {
				Map<Integer, String> header = new HashMap<Integer, String>();
				header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
				CoreProto.TransportPackageData.Builder packBuilder = CoreProto.TransportPackageData.newBuilder();
				packBuilder.putAllHeader(header);
				channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
						.add(RequestAction.IM_STC_PONG.getName()).add(packBuilder.build().toByteArray()));
				// 检测是否需要给用户发送PSN
				if (channelSession.detectPsn()) {
					channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
							.add(RequestAction.IM_STC_PSN.getName()).add(packBuilder.build().toByteArray()));
					logger.info("{} client={} siteUserId={} deviceId={} push psn {} {}", AkxProject.PLN,
							command.getClientIp(), command.getSiteUserId(), command.getDeviceId(),
							channelSession.getPsnTime(), channelSession.getSynFinTime());
				}

			}

			new ImMessageService().execute(command);

		} catch (Exception e) {
			logger.error("im request error.", e);
		}
		return null;
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
