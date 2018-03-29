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
package com.akaxin.common.netty;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.netty.codec.MessageDecoder;
import com.akaxin.common.netty.codec.MessageEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.SucceededFuture;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-19 18:14:16
 */
public class NettyClient2 {
	private static final Logger logger = LoggerFactory.getLogger(NettyClient2.class);
	private volatile ChannelPromise channelPromise;
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
	private final Bootstrap clientBoot = new Bootstrap();
	private Promise<IRedisCommandResponse> responsePromise;
	private static final Exception CONNECT_EXCEPTION = new Exception("client connect to server error");
	private NettyClientHandler nettyClientHandler;

	public NettyClient2() {
		try {
			clientBoot.option(ChannelOption.TCP_NODELAY, true);
			clientBoot.group(eventLoopGroup);
			clientBoot.channel(NioSocketChannel.class);
			clientBoot.option(ChannelOption.TCP_NODELAY, true);
			clientBoot.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel channel) throws Exception {
					channel.pipeline().addLast(new MessageEncoder());
					channel.pipeline().addLast(new MessageDecoder());
					channel.pipeline().addLast("timeout", new IdleStateHandler(20, 20, 0, TimeUnit.SECONDS));
					channel.pipeline().addLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS));

					nettyClientHandler = new NettyClientHandler(NettyClient2.this);
					channel.pipeline().addLast(nettyClientHandler);
				}

			});
		} catch (Exception e) {
			shutDownGracefully();
			logger.error("init netty client error.", e);
		}
	}

	public Future<Void> connect(String address, int port) {
		final Future<Void> connectionFuture;
		synchronized (clientBoot) {
			if (this.channelPromise == null) {
				try {
					final ChannelFuture connectFuture = this.clientBoot.connect(address, port).sync();
					this.channelPromise = connectFuture.channel().newPromise();

				} catch (Exception e) {
					logger.error("connect to akaxin platform error.", e);
				}

			}
			// if (this.channelPromise != null) {
			// logger.info("Finish this APNs connect isSuccess={} AND its channel
			// isActive={}",
			// this.channelPromise.isSuccess(), this.channelPromise.channel().isActive());
			// }
			connectionFuture = this.channelPromise;
		}
		// logger.info("connect to server connectionFuture={}", connectionFuture);
		return connectionFuture;
	}

	public void shutDownGracefully() {
		try {
			if (eventLoopGroup != null) {
				eventLoopGroup.shutdownGracefully();
				eventLoopGroup.terminationFuture().sync();
			}
			// logger.info("=========shut down gracefully==========");
		} catch (InterruptedException e) {
			logger.error("shutdown netty client error.", e);
		}
	}

	public Future<IRedisCommandResponse> sendRedisCommand(final RedisCommand redisCommand) {
		final Future<IRedisCommandResponse> responseFuture;
		// logger.info("send push message {} {} {}", channelPromise,
		// channelPromise.isSuccess(),
		// channelPromise.channel().isActive());
		if (channelPromise != null) {
			final ChannelPromise readyPromise = this.channelPromise;

			final DefaultPromise<IRedisCommandResponse> responsePromise = new DefaultPromise<IRedisCommandResponse>(
					readyPromise.channel().eventLoop());
			// 提交一个事件
			readyPromise.channel().eventLoop().submit(new Runnable() {
				@Override
				public void run() {
					// 将这个结果赋值给responsePromise
					NettyClient2.this.responsePromise = responsePromise;
				}
			});

			readyPromise.channel().writeAndFlush(redisCommand).addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(final ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						// 如果失败了，直接将promise返回
						responsePromise.tryFailure(future.cause());
						logger.error("send push message error: {},cause={}", redisCommand, future.cause());
					} else {
						// logger.info("write data to platform success");
					}
				}
			});
			responseFuture = responsePromise;
		} else {
			logger.error("send push error because client is not connected: {}", redisCommand.toString());
			responseFuture = new FailedFuture<IRedisCommandResponse>(GlobalEventExecutor.INSTANCE, CONNECT_EXCEPTION);
		}
		return responseFuture;
	}

	// 提交一次tcp请求结果
	protected void handleResponse(final IRedisCommandResponse response) {
		try {
			this.responsePromise.setSuccess(response);
		} catch (Exception e) {
			logger.error("handlePushNotificationResponse error!", e);
		}
	}

	public void disconnect() {
		// logger.info("close tcp socket, Disconnecting.");
		synchronized (this.clientBoot) {
			this.channelPromise = null;
			final Future<Void> channelCloseFuture;
			if (this.channelPromise != null) {
				channelCloseFuture = this.channelPromise.channel().close();
			} else {
				channelCloseFuture = new SucceededFuture<Void>(GlobalEventExecutor.INSTANCE, null);
			}
			channelCloseFuture.addListener(new GenericFutureListener<Future<Void>>() {
				@Override
				public void operationComplete(final Future<Void> future) throws Exception {
					NettyClient2.this.clientBoot.config().group().shutdownGracefully();
				}
			});
		}
		// logger.info("close netty tcp socket connection");
	}
}