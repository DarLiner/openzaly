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
package com.akaxin.common.netty.codec;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.RedisCommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 编码器
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.09.27
 * @since 2018-01-19 18:43:34
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
		int byteSize = msg.getByteSize();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
		msg.encode(byteBuffer);
		byte[] bytes = byteBuffer.array();
		out.writeBytes(bytes);
	}

}
