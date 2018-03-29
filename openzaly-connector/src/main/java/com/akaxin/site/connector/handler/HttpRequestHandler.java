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

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CharsetCoding;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.crypto.AESCrypto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.service.HttpRequestService;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.connector.constant.HttpConst;
import com.akaxin.site.connector.constant.PluginConst;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 使用TCP处理API请求,TCP代处理HTTP请求
 * 
 * @author Sam
 * @since 2017.10.19
 *
 * @param <Command>
 */
public class HttpRequestHandler extends AbstractCommonHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

	@Override
	public CommandResponse handle(Command command) {
		try {
			ChannelHandlerContext context = command.getChannelContext();
			if (context == null) {
				logger.error("{} client={} http request error context={}", AkxProject.PLN, command.getClientIp(),
						context);
				return null;
			}
			CommandResponse comamndResponse = new HttpRequestService().process(command);
			comamndResponse.setVersion(CommandConst.PROTOCOL_VERSION);
			comamndResponse.setAction(CommandConst.ACTION_RES);

			String authKey = command.getField(PluginConst.PLUGIN_AUTH_KEY, String.class);
			fullHttpResponse(context, comamndResponse, authKey);
			return comamndResponse;
		} catch (Exception e) {
			logger.error("api request error.", e);
		}
		return null;
	}

	/**
	 * response包含： <br>
	 * 1.状态行 ，Http-Version Status-Code Reason-Phrase CLRLF<br>
	 * 2.消息报头 <br>
	 * 3.响应正文
	 */
	private void fullHttpResponse(ChannelHandlerContext context, CommandResponse commandResponse, String authKey) {
		FullHttpResponse response = null;
		try {
			PluginProto.ProxyPluginPackage.Builder packBuilder = PluginProto.ProxyPluginPackage.newBuilder();
			CoreProto.ErrorInfo errInfo = CoreProto.ErrorInfo.newBuilder()
					.setCode(String.valueOf(commandResponse.getErrCode())).setInfo(commandResponse.getErrInfo())
					.build();
			packBuilder.setErrorInfo(errInfo);
			if (commandResponse.getParams() != null) {
				String dataStr = Base64.getEncoder().encodeToString(commandResponse.getParams());
				packBuilder.setData(dataStr);
			}

			byte[] httpResBytes = packBuilder.build().toByteArray();
			if (StringUtils.isNotEmpty(authKey)) {
				// authKey存在，则加密，不存在则使用原文
				byte[] tsk = authKey.getBytes(CharsetCoding.ISO_8859_1);
				byte[] decContent = AESCrypto.encrypt(tsk, httpResBytes);
				httpResBytes = decContent;
			}
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(httpResBytes));
		} catch (Exception e) {
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
			logger.error("full http response error.", e);
		}
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpConst.HTTP_H_CONTENT_TYPE);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		context.writeAndFlush(response);
	}

}
