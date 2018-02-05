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
package com.akaxin.common.channel;

import java.util.HashMap;
import java.util.Map;

import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.core.CoreProto;
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * write data to user,
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.10 14:06:11
 */
public class ChannelWriter {

	public static void write(Channel channel, RedisCommand redisCommand) {
		channel.writeAndFlush(redisCommand);
	}

	public static void writeAndClose(final Channel channel, RedisCommand redisCommand) {
		channel.writeAndFlush(redisCommand).addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				channel.close();
			}
		});
	}

	public static void write(Channel channel, CommandResponse response) {
		CoreProto.TransportPackageData.Builder packageDataBuilder = CoreProto.TransportPackageData.newBuilder();
		CoreProto.ErrorInfo errorInfo = CoreProto.ErrorInfo.newBuilder().setCode(response.getErrCode())
				.setInfo(String.valueOf(response.getErrInfo())).build();
		packageDataBuilder.setErr(errorInfo);

		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		packageDataBuilder.putAllHeader(header);

		if (response.getParams() != null) {
			packageDataBuilder.setData(ByteString.copyFrom(response.getParams()));
		}
		channel.writeAndFlush(new RedisCommand().add(response.getVersion()).add(response.getAction())
				.add(packageDataBuilder.build().toByteArray()));
	}

	public static void writeAndClose(final Channel channel, CommandResponse response) {
		CoreProto.TransportPackageData.Builder packageDataBuilder = CoreProto.TransportPackageData.newBuilder();
		CoreProto.ErrorInfo errorInfo = CoreProto.ErrorInfo.newBuilder().setCode(response.getErrCode())
				.setInfo(String.valueOf(response.getErrInfo())).build();
		packageDataBuilder.setErr(errorInfo);

		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		packageDataBuilder.putAllHeader(header);

		if (response.getParams() != null) {
			packageDataBuilder.setData(ByteString.copyFrom(response.getParams()));
		}
		channel.writeAndFlush(new RedisCommand().add(response.getVersion()).add(response.getAction())
				.add(packageDataBuilder.build().toByteArray()))
				.addListener(new GenericFutureListener<Future<? super Void>>() {

					@Override
					public void operationComplete(Future<? super Void> future) throws Exception {
						channel.close();
					}
				});
	}

	public static void writeByDeviceId(String deviceId, CommandResponse commandResponse) {
		ChannelSession channelSession = ChannelManager.getChannelSession(deviceId);
		if (channelSession != null && channelSession.getChannel() != null) {
			write(channelSession.getChannel(), commandResponse);
		}
	}

	public static void writeByDeviceId(String deviceId, RedisCommand redisCommand) {
		ChannelSession channelSession = ChannelManager.getChannelSession(deviceId);
		if (channelSession != null && channelSession.getChannel() != null) {
			write(channelSession.getChannel(), redisCommand);
		}
	}

}