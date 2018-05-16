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
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.executor.chain.handler.MethodReflectHandler;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.site.ImSiteAuthProto;
import com.akaxin.proto.site.ImSiteHelloProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.connector.session.SessionManager;
import com.akaxin.site.storage.bean.SimpleAuthBean;

import io.netty.channel.Channel;

/**
 * <pre>
 * 处理用户登陆站点之前认证行为 
 * 		1.hello行为，获取站点版本号<im.site.hello> 
 * 		2.auth行为，进行用户个人身份认证<im.site.auth>
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.15
 *
 * @param <Command>
 */
public class ImSiteAuthHandler extends MethodReflectHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ImSiteAuthHandler.class);
	private static final int IM = 1;

	public CommandResponse hello(Command command) {
		ChannelSession channelSession = command.getChannelSession();
		try {
			if (channelSession != null) {
				ImSiteHelloProto.ImSiteHelloRequest request = ImSiteHelloProto.ImSiteHelloRequest
						.parseFrom(command.getParams());
				String clientVersion = request.getClientVersion();
				logger.debug("{} client={} siteUserId={} action={} clientVersion={}", AkxProject.PLN,
						command.getClientIp(), command.getSiteUserId(), RequestAction.IM_SITE_HELLO, clientVersion);
				return helloResponse(channelSession.getChannel());
			}
		} catch (Exception e) {
			logger.error(StringHelper.format("{} client={} siteUserId={} action={} error.", AkxProject.PLN,
					command.getClientIp(), command.getSiteUserId(), RequestAction.IM_SITE_HELLO), e);
		}
		return defaultErrorResponse();
	}

	private CommandResponse helloResponse(Channel channel) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ImSiteHelloProto.ImSiteHelloResponse response = ImSiteHelloProto.ImSiteHelloResponse.newBuilder()
				.setSiteVersion(CommandConst.SITE_VERSION).build();
		commandResponse.setErrCode2(ErrorCode2.SUCCESS);
		commandResponse.setParams(response.toByteArray());
		ChannelWriter.write(channel, commandResponse);
		return commandResponse;
	}

	public CommandResponse auth(Command command) {
		ChannelSession channelSession = command.getChannelSession();
		if (channelSession != null) {
			boolean authResult = authentication(channelSession, command);
			return authResponse(channelSession.getChannel(), command, authResult);
		}
		return defaultErrorResponse();
	}

	private boolean authentication(ChannelSession channelSession, Command command) {
		try {
			ImSiteAuthProto.ImSiteAuthRequest request = ImSiteAuthProto.ImSiteAuthRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			String sessionId = request.getSiteSessionId();
			command.setSiteUserId(siteUserId);

			if (StringUtils.isNoneEmpty(siteUserId, sessionId)) {
				SimpleAuthBean authSession = SessionManager.getInstance().getAuthSession(sessionId);
				logger.debug("{} client={} siteUserId={} action={} sessionId={} authSession={}", AkxProject.PLN,
						command.getClientIp(), siteUserId, RequestAction.IM_SITE_AUTH, sessionId, authSession);

				if (authSession != null && siteUserId.equals(authSession.getSiteUserId())) {
					// 1. set user online
					SessionManager.getInstance().setUserOnline(siteUserId, authSession.getDeviceId());
					// 2. Mark IM长链接
					channelSession.setCtype(IM);
					channelSession.setUserId(siteUserId);
					channelSession.setDeviceId(authSession.getDeviceId());
					ChannelManager.addChannelSession(authSession.getDeviceId(), channelSession);
					// 3. update active time
					SessionManager.getInstance().updateActiveTime(siteUserId, authSession.getDeviceId());
					// 4. log
					logger.debug("{} client={} siteUserId={} action={} AUTH SUCCESS ChannelSessionSize{}",
							AkxProject.PLN, command.getClientIp(), command.getSiteUserId(), RequestAction.IM_SITE_AUTH,
							ChannelManager.getChannelSessionSize());
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(StringHelper.format("{} client={} siteUserId={} auth session error......", AkxProject.PLN,
					command.getClientIp(), command.getSiteUserId()), e);
		}
		return false;
	}

	private CommandResponse authResponse(Channel channel, Command command, boolean result) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR_SESSION;
		if (result) {
			String siteServer = SiteConfig.getSiteAddress();
			ImSiteAuthProto.ImSiteAuthResponse authResponse = ImSiteAuthProto.ImSiteAuthResponse.newBuilder()
					.setSiteServer(siteServer).build();
			commandResponse.setParams(authResponse.toByteArray());
			errCode = ErrorCode2.SUCCESS;
			ChannelWriter.write(channel, commandResponse.setErrCode2(errCode));
		} else {
			ChannelWriter.writeAndClose(channel, commandResponse.setErrCode2(errCode));
		}
		logger.debug("{} client={} siteUserId={} action={} auth response result={}", AkxProject.PLN,
				command.getClientIp(), command.getSiteUserId(), RequestAction.IM_SITE_AUTH, errCode.toString());
		return commandResponse;
	}

	private CommandResponse defaultErrorResponse() {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		commandResponse.setErrCode2(ErrorCode2.ERROR);
		return commandResponse;
	}
}
