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

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.site.business.service.ApiRequestService;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.akaxin.site.storage.service.UserSessionDaoService;
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 使用TCP处理API请求,TCP代处理HTTP请求
 * 
 * @author Sam
 * @since 2017.10.19
 *
 * @param <Command>
 */
public class ApiRequestHandler extends AbstractCommonHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiRequestHandler.class);

	public CommandResponse handle(Command command) {
		try {
			ChannelSession channelSession = command.getChannelSession();
			if (channelSession == null || channelSession.getChannel() == null) {
				logger.error("{} client={} api request handler error.channelSession={}", AkxProject.PLN,
						command.getClientIp(), channelSession);
				// NULL，则不需要再次关闭channel
				return null;
			}

			switch (RequestAction.getAction(command.getAction())) {
			case API_SITE_CONFIG:
			case API_SITE_REGISTER:
			case API_SITE_LOGIN:
				break;
			default: {
				Map<Integer, String> header = command.getHeader();
				String sessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);
				// logger.debug("{} client={} api request sessionId={}", AkxProject.PLN,
				// command.getClientIp(), sessionId);

				if (!StringUtils.isNotEmpty(sessionId)) {
					this.tellClientSessionError(channelSession.getChannel());
					logger.error("{} client={} api request with sessionId is NULL", AkxProject.PLN,
							command.getClientIp());
					return null;
				}

				IUserSessionDao sessionDao = new UserSessionDaoService();
				SimpleAuthBean authBean = sessionDao.getUserSession(sessionId);
				// logger.debug("{} client={} api session auth result {}", authBean.toString());

				if (authBean == null || StringUtils.isAnyEmpty(authBean.getSiteUserId(), authBean.getDeviceId())) {
					this.tellClientSessionError(channelSession.getChannel());
					logger.error("{} client={} api session auth fail.authBean={}", AkxProject.PLN,
							command.getClientIp(), authBean.toString());
					return null;
				}

				command.setSiteUserId(authBean.getSiteUserId());
				command.setDeviceId(authBean.getDeviceId());
			}
			}
			// 执行业务操作
			return this.doApiRequest(channelSession.getChannel(), command);
		} catch (Exception e) {
			logger.error(StringHelper.format("{} client={} api request error.", AkxProject.PLN, command.getClientIp()),
					e);
		}
		return null;
	}

	private CommandResponse doApiRequest(final Channel channel, Command command) {
		CommandResponse comamndResponse = new ApiRequestService().process(command);
		// response
		CoreProto.TransportPackageData.Builder packageBuilder = CoreProto.TransportPackageData.newBuilder();
		// header
		Map<Integer, String> header = new HashMap<Integer, String>();
		// 站点业务版本（proto版本）
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		packageBuilder.putAllHeader(header);
		// errCode
		CoreProto.ErrorInfo errinfo = CoreProto.ErrorInfo.newBuilder()
				.setCode(String.valueOf(comamndResponse.getErrCode())).setInfo(comamndResponse.getErrInfo()).build();
		packageBuilder.setErr(errinfo);
		// data
		if (comamndResponse.getParams() != null) {
			packageBuilder.setData(ByteString.copyFrom(comamndResponse.getParams())).build();
		}
		// 协议版本 CommandConst.PROTOCOL_VERSION=1.0
		String protocolVersion = CommandConst.PROTOCOL_VERSION;
		String action = comamndResponse.getAction() == null ? CommandConst.ACTION_RES : comamndResponse.getAction();
		channel.writeAndFlush(
				new RedisCommand().add(protocolVersion).add(action).add(packageBuilder.build().toByteArray()))
				.addListener(new GenericFutureListener<Future<? super Void>>() {

					public void operationComplete(Future<? super Void> future) throws Exception {
						channel.close();
					}
				});
		return comamndResponse;
	}

	private void tellClientSessionError(final Channel channel) {
		// response
		CoreProto.TransportPackageData.Builder packageBuilder = CoreProto.TransportPackageData.newBuilder();
		// header
		Map<Integer, String> header = new HashMap<Integer, String>();
		// 站点业务版本（proto版本）
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		packageBuilder.putAllHeader(header);
		// errCode
		CoreProto.ErrorInfo errInfo = CoreProto.ErrorInfo.newBuilder().setCode(ErrorCode2.ERROR_SESSION.getCode())
				.setInfo(ErrorCode2.ERROR_SESSION.getInfo()).build();
		packageBuilder.setErr(errInfo);

		// 协议版本 CommandConst.PROTOCOL_VERSION=1.0
		String protocolVersion = CommandConst.PROTOCOL_VERSION;
		String action = CommandConst.ACTION_RES;
		channel.writeAndFlush(
				new RedisCommand().add(protocolVersion).add(action).add(packageBuilder.build().toByteArray()))
				.addListener(new GenericFutureListener<Future<? super Void>>() {

					public void operationComplete(Future<? super Void> future) throws Exception {
						channel.close();
					}
				});
	}

}
