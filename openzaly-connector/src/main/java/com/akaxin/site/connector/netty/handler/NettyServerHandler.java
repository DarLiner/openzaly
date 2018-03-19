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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.site.connector.codec.parser.ParserConst;
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
	private AbstracteExecutor<Command> executor;

	public NettyServerHandler(AbstracteExecutor<Command> executor) {
		this.executor = executor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(ParserConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));
		logger.info("open netty channel connection... client={}", ctx.channel().toString());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("close netty channel connection...client={}", ctx.channel().toString());

		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		if (channelSession.getCtype() == 1 && StringUtils.isNotEmpty(channelSession.getUserId())) {
			ChannelManager.delChannelSession(channelSession.getDeviceId());
			String siteUserId = channelSession.getUserId();
			String deviceId = channelSession.getDeviceId();
			boolean offResult = SessionManager.getInstance().setUserOffline(siteUserId, deviceId);

			logger.info("User Offline:{}. siteUserId={} deviceId={} ChannelSessionKey={}", offResult, siteUserId,
					deviceId, GsonUtils.toJson(ChannelManager.getChannelSessionKeySet()));
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RedisCommand redisCmd) throws Exception {
		// 获取channel以及channel绑定的信息
		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();

		/**
		 * 如果channel不活跃，关闭tcp连接
		 */
		if (channelSession.getChannel() == null || !channelSession.getChannel().isActive()) {
			ctx.disconnect();// 关闭tcp连接
		}

		String version = redisCmd.getParameterByIndex(0);
		String action = redisCmd.getParameterByIndex(1);
		byte[] params = redisCmd.getBytesParamByIndex(2);
		CoreProto.TransportPackageData packageData = CoreProto.TransportPackageData.parseFrom(params);

		Command command = new Command();
		command.setSiteUserId(channelSession.getUserId());
		command.setDeviceId(channelSession.getDeviceId());
		command.setAction(action);
		command.setHeader(packageData.getHeaderMap());
		command.setParams(packageData.getData().toByteArray());
		command.setChannelSession(channelSession);

		if (!RequestAction.IM_CTS_PING.getName().equalsIgnoreCase(command.getAction())) {
			LogUtils.printNetLog(logger, "c->s", version, action, "", "", params.length);
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
			this.executor.execute(command.getRety(), command);
		} else {
			logger.warn("unknow request command={}", command.toString());
			return;
		}
	}

	/**
	 * 比较严格的处理方式，channel处理异常，直接关闭链接，客户端此时需要重新连接到服务端
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause != null) {
			logger.error("channel exeception happen.", cause);
		}
		ctx.close();
	}

	/**
	 * 设置idle，触发此方法
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		logger.info("user event triggered evt={}", evt.toString());
	}

}