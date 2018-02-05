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
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:32:50
 */
public class NettyClient {
	private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
	private volatile ChannelPromise channelPromise;
	private EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap clientBoot;

	public NettyClient() {
		try {
			clientBoot = new Bootstrap();
			clientBoot.option(ChannelOption.TCP_NODELAY, true);
			clientBoot.group(group).channel(NioSocketChannel.class);
			clientBoot.option(ChannelOption.TCP_NODELAY, true);
			clientBoot.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel channel) throws Exception {
					channel.pipeline().addLast(new MessageEncoder());
					channel.pipeline().addLast("timeout", new IdleStateHandler(2000, 2000, 0, TimeUnit.MICROSECONDS));
					channel.pipeline().addLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS));
				}

			});
		} catch (Exception e) {
			shutDownGracefully();
			logger.error("init netty client error.", e);
		}
	}

	public void connect(String address, int port) {
		synchronized (clientBoot) {

			try {
				if (channelPromise == null) {

					ChannelFuture f = clientBoot.connect(address, port).sync();
					channelPromise = f.channel().newPromise();

					logger.info("connect to akaxin platform server......");
					// 监听当channel被关闭
					f.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
						@Override
						public void operationComplete(final ChannelFuture future) throws Exception {

							synchronized (clientBoot) {

								future.channel().eventLoop().schedule(new Runnable() {
									@Override
									public void run() {
										logger.info("netty client lost the connection");
									}
								}, 10, TimeUnit.SECONDS);

							}
							future.channel().eventLoop().submit(new Runnable() {
								@Override
								public void run() {
									logger.info("event loop submit!");
								}
							});
						}
					});
				}
			} catch (Exception e) {
				logger.error("connect to akaxin platform error.", e);
			}

		}

	}

	public void shutDownGracefully() {
		try {
			if (group != null) {
				group.shutdownGracefully();
				group.terminationFuture().sync();
			}
		} catch (InterruptedException e) {
			logger.error("shutdown netty client error.", e);
		}
	}

	public void send(RedisCommand cmd) {
		try {
			if (channelPromise != null) {
				channelPromise.channel().writeAndFlush(cmd).addListener(new GenericFutureListener<ChannelFuture>() {
					@Override
					public void operationComplete(final ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							logger.info("send data to paltform failed");
						} else {
							logger.info("send push data success.");
						}

					}
				});
			}
		} catch (Exception e) {
			logger.error("netty client send data error.", e);
		}
	}
}
