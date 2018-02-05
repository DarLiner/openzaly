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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.chain.MethodReflectHandler;
import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.utils.ServerAddressUtils;
import com.akaxin.proto.site.ImSiteAuthProto;
import com.akaxin.proto.site.ImSiteHelloProto;
import com.akaxin.site.connector.session.SessionManager;
import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.channel.Channel;

/**
 * 处理用户登陆站点之前认证行为 <br>
 * 1.hello行为，获取站点版本号<im.site.hello> <br>
 * 2.auth行为，进行用户个人身份认证<im.site.auth> <br>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.15
 *
 * @param <Command>
 */
public class ImSiteAuthHandler extends MethodReflectHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImSiteAuthHandler.class);

	public boolean hello(Command command) {
		logger.info("----------Im hello Handler--------");
		ChannelSession channelSession = command.getChannelSession();
		try {
			if (channelSession != null) {
				ImSiteHelloProto.ImSiteHelloRequest request = ImSiteHelloProto.ImSiteHelloRequest
						.parseFrom(command.getParams());
				String clientVersion = request.getClientVersion();

				logger.info("im hello request, get clientVersion={}", clientVersion);
				helloResponse(channelSession.getChannel());
				return true;
			}
		} catch (InvalidProtocolBufferException e) {
			logger.error("im hello request error.", e);
		}
		return false;
	}

	private void helloResponse(Channel channel) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ImSiteHelloProto.ImSiteHelloResponse response = ImSiteHelloProto.ImSiteHelloResponse.newBuilder()
				.setSiteVersion(CommandConst.SITE_VERSION).build();
		commandResponse.setErrCode(ErrorCode.SUCCESS);
		commandResponse.setParams(response.toByteArray());
		ChannelWriter.write(channel, commandResponse);
	}

	public boolean auth(Command command) {
		logger.info("im.site.auth command={}", command.toString());
		boolean authResult = false;
		ChannelSession channelSession = command.getChannelSession();
		if (channelSession != null) {
			authResult = authentication(channelSession, command);
			authResponse(channelSession.getChannel(), command, authResult);
		}
		return authResult;
	}

	private boolean authentication(ChannelSession channelSession, Command command) {
		try {
			ImSiteAuthProto.ImSiteAuthRequest request = ImSiteAuthProto.ImSiteAuthRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			String sessionId = request.getSiteSessionId();

			logger.info("siteUserId={},sessionId={}", siteUserId, sessionId);

			SimpleAuthBean authSessionBean = SessionManager.getInstance().getAuthSession(sessionId);

			logger.info("判断session库里是否存在用户，authSessionBean={}", authSessionBean.toString());

			if (siteUserId.equals(authSessionBean.getSiteUserId())) {
				// set user online
				SessionManager.getInstance().setUserOnline(siteUserId, authSessionBean.getDeviceId());
				// Mark IM长链接
				channelSession.setCtype(1);
				channelSession.setUserId(siteUserId);
				channelSession.setDeviceId(authSessionBean.getDeviceId());
				ChannelManager.addChannelSession(authSessionBean.getDeviceId(), channelSession);

				SessionManager.getInstance().updateActiveTime(siteUserId, authSessionBean.getDeviceId());
				logger.info("im.site.auth success. ChannelSession={},ChannelSessionSize{}",
						ChannelManager.getChannelSessions(), ChannelManager.getChannelSessionSize());

				return true;
			}

			logger.info("auth fail for siteUserId={},authSiteUserId={}.", siteUserId, authSessionBean.getSiteUserId());
		} catch (Exception e) {
			logger.error("process auth session error......", e);
		}

		return false;
	}

	private void authResponse(Channel channel, Command command, boolean result) {
		logger.info("im.site.auth session response");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		if (result) {
			String siteServer = ServerAddressUtils.getAddressPort();
			ImSiteAuthProto.ImSiteAuthResponse authResponse = ImSiteAuthProto.ImSiteAuthResponse.newBuilder()
					.setSiteServer(siteServer).build();
			commandResponse.setParams(authResponse.toByteArray());
			ChannelWriter.write(channel, commandResponse.setErrCode2(ErrorCode2.SUCCESS));
			logger.info("im.site.auth success result={}", ErrorCode2.SUCCESS);
		} else {
			ChannelWriter.writeAndClose(channel, commandResponse.setErrCode2(ErrorCode2.ERROR2_IMAUTH_FAIL));
			logger.error("im.site.auth fail result={}", ErrorCode2.ERROR2_IMAUTH_FAIL);
		}
	}

}
