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
package com.akaxin.site.connector.netty.handler;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.site.connector.codec.parser.ParserConst;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.connector.session.SessionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty处理TCP链接中接受客户端传入的消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.07 16:56:36
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RedisCommand> {
	private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
	private AbstracteExecutor<Command, CommandResponse> executor;

	public NettyServerHandler(AbstracteExecutor<Command, CommandResponse> executor) {
		this.executor = executor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// connect to netty server
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = socketAddress.getAddress().getHostAddress();
		ctx.channel().attr(ParserConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));
		logger.debug("{} client={} connect to Netty Server...", AkxProject.PLN, clientIp);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = socketAddress.getAddress().getHostAddress();
		logger.debug("{} client={} close connection... ChannelSize={}", AkxProject.PLN, clientIp,
				ChannelManager.getChannelSessionSize());

		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		if (channelSession.getCtype() == 1 && StringUtils.isNotEmpty(channelSession.getUserId())) {
			ChannelManager.delChannelSession(channelSession.getDeviceId());
			String siteUserId = channelSession.getUserId();
			String deviceId = channelSession.getDeviceId();
			boolean offRes = SessionManager.getInstance().setUserOffline(siteUserId, deviceId);

			logger.debug("{} set client={} siteUserId={} deviceId={} offline-status:{} ChannelSize={}", AkxProject.PLN,
					clientIp, siteUserId, deviceId, offRes, ChannelManager.getChannelSessionSize());
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RedisCommand redisCmd) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = socketAddress.getAddress().getHostAddress();
		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();

		// 如果channel不活跃，关闭tcp连接
		if (channelSession.getChannel() == null || !channelSession.getChannel().isActive()) {
			ctx.disconnect();// 关闭tcp连接
			logger.warn("{} close client={} as its channel is not active ", AkxProject.PLN, clientIp);
		}

		String version = redisCmd.getParameterByIndex(0);
		String action = redisCmd.getParameterByIndex(1);
		byte[] params = redisCmd.getBytesParamByIndex(2);
		CoreProto.TransportPackageData packageData = CoreProto.TransportPackageData.parseFrom(params);

		Command command = new Command();
		command.setClientIp(clientIp);
		command.setSiteUserId(channelSession.getUserId());
		command.setDeviceId(channelSession.getDeviceId());
		command.setAction(action);
		command.setHeader(packageData.getHeaderMap());
		command.setParams(packageData.getData().toByteArray());
		command.setChannelSession(channelSession);
		command.setStartTime(System.currentTimeMillis());

		if (!RequestAction.IM_CTS_PING.getName().equalsIgnoreCase(command.getAction())) {
			logger.debug("{} client={} -> site version={} action={} params-length={}", AkxProject.PLN, clientIp,
					version, action, params.length);
		} else {
			logger.trace("{} client={} ping -> site", AkxProject.PLN, clientIp);
		}

		if (RequestAction.IM.getName().equals(command.getRety())) {
			// 如果是syncFinish，则这里需要修改channel中的syncFinTime
			channelSession.setActionForPsn(action);
			// 单独处理im.site.hello && im.site.auth
			if (RequestAction.SITE.getName().equalsIgnoreCase(command.getService())) {
				String anoRequest = command.getRety() + "." + command.getService();
				command.setRety(anoRequest);
			}
			this.executor.execute(command.getRety(), command);
		} else if (RequestAction.API.getName().equalsIgnoreCase(command.getRety())) {
			CommandResponse response = this.executor.execute(command.getRety(), command);
			LogUtils.requestResultLog(logger, command, response);
		} else {
			logger.warn("{} client={} siteUserId={} action={} unknow request method", AkxProject.PLN,
					command.getClientIp(), command.getSiteUserId(), command.getAction());
			return;
		}

		logger.debug("{} client={} siteUserId={} action={} ", AkxProject.PLN, command.getClientIp(),
				command.getSiteUserId(), command.getAction(), System.currentTimeMillis() - command.getStartTime());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = socketAddress.getAddress().getHostAddress();
		ctx.close();
		logger.error(StringHelper.format("{} client{} channel exeception happen.", AkxProject.PLN, clientIp), cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// 设置idle，触发此方法
		// logger.info("user event triggered evt={}", evt.toString());
	}

}