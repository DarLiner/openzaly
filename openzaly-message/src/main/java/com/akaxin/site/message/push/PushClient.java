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
package com.akaxin.site.message.push;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.netty.IRedisCommandResponse;
import com.akaxin.common.netty.PlatformSSLClient;
import com.akaxin.proto.core.CoreProto;
import com.google.protobuf.ByteString;

import io.netty.util.concurrent.Future;

/**
 * 通过平台发送push客户端
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-20 21:45:07
 */
public class PushClient {
	private static final Logger logger = LoggerFactory.getLogger(PushClient.class);

	private static final String AKAXIN_PUSH_ADDRESS = "push.akaxin.com";
	private static final int AKAXIN_PUSH_PORT = 443;

	private PushClient() {
	}

	public static byte[] syncWrite(String action, byte[] byteData) {
		try {
			CoreProto.TransportPackageData.Builder packageDataBuilder = CoreProto.TransportPackageData.newBuilder();
			Map<Integer, String> header = new HashMap<Integer, String>();
			header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
			packageDataBuilder.putAllHeader(header);
			packageDataBuilder.setData(ByteString.copyFrom(byteData));
			PlatformSSLClient nettyClient = new PlatformSSLClient();
			nettyClient.connect(AKAXIN_PUSH_ADDRESS, AKAXIN_PUSH_PORT);
			Future<IRedisCommandResponse> future = nettyClient.sendRedisCommand(new RedisCommand()
					.add(CommandConst.PROTOCOL_VERSION).add(action).add(packageDataBuilder.build().toByteArray()));
			IRedisCommandResponse response = future.get(5, TimeUnit.SECONDS);
			nettyClient.disconnect();
			if (response != null && response.isSuccess()) {
				return getResponseBytes(response.getRedisCommand());
			}
			logger.debug("write push to platform finish response={}", response);
		} catch (Exception e) {
			logger.error("sync send package error ", e);
		}
		return null;
	}

	public static void asyncWrite(String action, byte[] byteData) {
		try {
			CoreProto.TransportPackageData.Builder packageDataBuilder = CoreProto.TransportPackageData.newBuilder();
			Map<Integer, String> header = new HashMap<Integer, String>();
			header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
			packageDataBuilder.putAllHeader(header);
			packageDataBuilder.setData(ByteString.copyFrom(byteData));
			PlatformSSLClient nettyClient = new PlatformSSLClient();
			nettyClient.connect(AKAXIN_PUSH_ADDRESS, AKAXIN_PUSH_PORT);
			Future<IRedisCommandResponse> future = nettyClient.sendRedisCommand(new RedisCommand()
					.add(CommandConst.PROTOCOL_VERSION).add(action).add(packageDataBuilder.build().toByteArray()));
			IRedisCommandResponse response = future.get(5, TimeUnit.SECONDS);
			logger.debug("write push to platform finish response={}", response);
			nettyClient.disconnect();
		} catch (Exception e) {
			logger.error("async send package to platform error", e);
		}
	}

	private static byte[] getResponseBytes(RedisCommand redisCommand) {
		try {
			String version = redisCommand.getParameterByIndex(0);
			String action = redisCommand.getParameterByIndex(1);
			byte[] params = redisCommand.getBytesParamByIndex(2);
			logger.debug("get package bytes from platform {},{},{}", version, action, params);
			if (CommandConst.ACTION_RES.equals(action)) {
				CoreProto.TransportPackageData packageData = CoreProto.TransportPackageData.parseFrom(params);
				CoreProto.ErrorInfo error = packageData.getErr();
				if ("success".equals(error.getCode())) {
					return packageData.getData().toByteArray();
				}
			}
		} catch (Exception e) {
			logger.error("get package bytes eror,redisCommand={}", redisCommand.toString());
		}
		return null;
	}
}