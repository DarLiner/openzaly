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
package com.akaxin.site.connector.codec.protocol;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.site.connector.codec.parser.ParserConst;
import com.akaxin.site.connector.constant.AkxProject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 编码器
 * 
 * @author Sam
 * @since 2017.09.27
 * 
 */
public class MessageEncoder extends MessageToByteEncoder<RedisCommand> {
	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		promise.addListener(new GenericFutureListener<Future<? super Void>>() {

			public void operationComplete(Future<? super Void> future) throws Exception {
				if (!future.isSuccess()) {
					logger.error("write data to client fail ", future.cause());
				}
			}
		});

		super.write(ctx, msg, promise);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RedisCommand msg, ByteBuf out) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = socketAddress.getAddress().getHostAddress();
		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		String version = msg.getParameterByIndex(0);
		String action = msg.getParameterByIndex(1);
		byte[] params = msg.getBytesParamByIndex(2);
		channelSession.setActionForPsn(action);

		if (!RequestAction.IM_STC_PONG.getName().equals(action)) {
			logger.debug("{} site -> client={}  version={} action={} params-length={}", AkxProject.PLN, clientIp,
					version, action, params.length);
		}

		int byteSize = msg.getByteSize();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
		msg.encode(byteBuffer);
		byte[] bytes = byteBuffer.array();
		out.writeBytes(bytes);

	}

}
