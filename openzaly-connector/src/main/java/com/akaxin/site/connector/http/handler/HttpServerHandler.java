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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.HttpUriAction;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.proto.core.PluginProto;

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
	private AbstracteExecutor<Command> executor;

	public HttpServerHandler(AbstracteExecutor<Command> executor) {
		this.executor = executor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client connect to http server... client={}", ctx.channel().toString());
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
					logger.error("http request method error. please use post!");
					ctx.close();
					return;
				}

				String clientIp = request.headers().get("X-Forwarded-For");
				if (clientIp == null) {
					InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
					clientIp = address.getAddress().getHostAddress();
				}

				if (!checkLegalClientIp(clientIp)) {
					logger.error("http request illegal request IP.");
					ctx.close();
					return;
				}
				logger.info("request uri:{} client ip={}", request.uri(), clientIp);
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

				byte[] contentBytes = new byte[httpByteBuf.readableBytes()];
				httpByteBuf.readBytes(contentBytes);
				httpByteBuf.release();

				PluginProto.ProxyPackage proxyPack = PluginProto.ProxyPackage.parseFrom(contentBytes);
				Map<Integer, String> proxyMap = proxyPack.getProxyContentMap();

				Command command = new Command();
				if (proxyMap != null) {
					command.setSiteUserId(proxyMap.get(PluginProto.ProxyKey.CLIENT_SITE_USER_ID_VALUE));
				}
				command.setChannelContext(ctx);
				command.setUri(request.uri());
				command.setParams(proxyPack.getDataBytes().toByteArray());

				logger.info("http server handler command={}", command.toString());

				this.executor.execute(HttpUriAction.HTTP_ACTION.getUri(), command);

			}
		} catch (Exception e) {
			logger.error("http request error.", e);
			ctx.close();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("channel exception caught = {}", cause);
		ctx.close();
	}

	/**
	 * 所有扩展请求站点的http服务请求，全部使用post请求
	 * 
	 * @return
	 */
	private boolean checkLegalRequest() {
		String methodName = request.method().name();
		if ("POST".equals(methodName)) {
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
