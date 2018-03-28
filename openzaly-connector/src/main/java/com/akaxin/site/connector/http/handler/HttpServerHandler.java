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
package com.akaxin.site.connector.http.handler;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CharsetCoding;
import com.akaxin.common.constant.HttpUriAction;
import com.akaxin.common.crypto.AESCrypto;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.connector.constant.HttpConst;
import com.akaxin.site.connector.constant.PluginConst;
import com.akaxin.site.connector.session.PluginSession;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * 处理Http请求
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-28 18:49:38
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

	private HttpRequest request;
	private AbstracteExecutor<Command, CommandResponse> executor;
	
	public HttpServerHandler(AbstracteExecutor<Command, CommandResponse> executor) {
		this.executor = executor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("{} client connect to http server... client={}", AkxProject.PLN, ctx.channel().toString());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			/**
			 * 一次Http请求，分两次处理，第一次处理Http消息头，第二次请求http消息体
			 */
			/**
			 * http-request包含： <br>
			 * 1.请求行 ,Method Request-URI Http-Version CRLF<br>
			 * 2.消息头 <br>
			 * 3.请求正文 <br>
			 */
			if (msg instanceof HttpRequest) {
				request = (HttpRequest) msg;
				if (!checkLegalRequest()) {
					logger.error("{} http request method error. please use post!", AkxProject.PLN);
					ctx.close();
					return;
				}

				String clientIp = request.headers().get(HttpConst.HTTP_H_FORWARDED);
				if (clientIp == null) {
					InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
					clientIp = address.getAddress().getHostAddress();
				}

				if (!checkLegalClientIp(clientIp)) {
					logger.error("{} http request illegal IP={}.", AkxProject.PLN, clientIp);
					ctx.close();
					return;
				}

				logger.debug("{} request uri:{} clientIp={}", AkxProject.PLN, request.uri(), clientIp);
			}

			/**
			 * HttpContent:表示HTTP实体正文和内容标头的基类 <br>
			 * method.name=POST 传输消息体存在内容
			 */
			if (msg instanceof LastHttpContent) {
				HttpContent content = (HttpContent) msg;
				ByteBuf httpByteBuf = content.content();
				if (httpByteBuf == null) {
					return;
				}

				if (!checkLegalRequest()) {
					ctx.close();
					return;
				}

				String clientIp = request.headers().get(HttpConst.HTTP_H_FORWARDED);
				String sitePluginId = request.headers().get(PluginConst.SITE_PLUGIN_ID);
				byte[] contentBytes = new byte[httpByteBuf.readableBytes()];
				httpByteBuf.readBytes(contentBytes);
				httpByteBuf.release();

				// 查询扩展的auth——key
				String authKey = PluginSession.getInstance().getPluginAuthKey(sitePluginId);
				if (StringUtils.isNotEmpty(authKey)) {
					// byte[] tsk = AESCrypto.generateTSKey(authKey);
					byte[] tsk = authKey.getBytes(CharsetCoding.ISO_8859_1);
					byte[] decContent = AESCrypto.decrypt(tsk, contentBytes);
					contentBytes = decContent;
				}

				PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(contentBytes);
				Map<Integer, String> proxyHeader = pluginPackage.getPluginHeaderMap();

				String requestTime = proxyHeader.get(PluginProto.PluginHeaderKey.PLUGIN_TIMESTAMP_VALUE);
				long currentTime = System.currentTimeMillis();
				boolean timeOut = true;
				if (StringUtils.isNotEmpty(requestTime)) {
					long timeMills = Long.valueOf(requestTime);
					if (currentTime - timeMills < 10 * 1000l) {
						timeOut = false;
					}
				}

				logger.debug("{} client={} http request timeOut={} currTime={} reqTime={}", AkxProject.PLN, clientIp,
						timeOut, currentTime, requestTime);

				if (!timeOut) {
					Command command = new Command();
					command.setField(PluginConst.PLUGIN_AUTH_KEY, authKey);
					if (proxyHeader != null) {
						command.setSiteUserId(proxyHeader.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE));
					}
					command.setChannelContext(ctx);
					command.setUri(request.uri());
					command.setParams(Base64.getDecoder().decode(pluginPackage.getData()));
					command.setClientIp(clientIp);
					command.setStartTime(System.currentTimeMillis());

					logger.debug("{} client={} http server handler command={}", AkxProject.PLN, clientIp,
							command.toString());

					this.executor.execute(HttpUriAction.HTTP_ACTION.getUri(), command);
				} else {
					// 超时10s，认为此请求失效，直接断开连接
					ctx.close();
					logger.error("{} client={} http request error.timeOut={} currTime={} reqTime={}", AkxProject.PLN,
							clientIp, timeOut, currentTime, requestTime);
				}
			}
		} catch (Exception e) {
			ctx.close();
			logger.error(StringHelper.format("{} http request error.", AkxProject.PLN), e);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		logger.error(StringHelper.format("{} channel exception caught", AkxProject.PLN), cause);
	}

	/**
	 * 所有扩展请求站点的http服务请求，全部使用post请求
	 * 
	 * @return
	 */
	private boolean checkLegalRequest() {
		String methodName = request.method().name();
		if (HttpConst.HTTP_M_POST.equals(methodName)) {
			return true;
		}
		return false;
	}

	// 预留处理请求ip过滤
	private boolean checkLegalClientIp(String ip) {
		// #TODO
		// logger.info("do nothing to http client ip:{}", ip);
		return true;
	}
}
