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
package com.akaxin.site.connector.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.site.connector.http.handler.HttpServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class HttpServer {
	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private ServerBootstrap bootstrap;
	private EventLoopGroup parentGroup;
	private EventLoopGroup childGroup;
	private AbstracteExecutor<Command> executor;

	public HttpServer() {
		try {
			executor = new SimpleExecutor<Command>();
			loadExecutor(executor);
			bootstrap = new ServerBootstrap();
			parentGroup = new NioEventLoopGroup();
			childGroup = new NioEventLoopGroup();
			bootstrap.group(parentGroup, childGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			// 接受连接的可连接队列大小
			bootstrap.option(ChannelOption.SO_BACKLOG, 120);
			bootstrap.option(ChannelOption.SO_REUSEADDR, true);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new HttpResponseEncoder());
					ch.pipeline().addLast(new HttpRequestDecoder());
					ch.pipeline().addLast(new HttpServerHandler(executor));
				}
			});
		} catch (Exception e) {
			closeGracefylly();
			logger.error("init http server error.", e);
			System.exit(-200);
		}
	}

	public void start(String address, int port) {
		try {
			ChannelFuture channelFuture = bootstrap.bind(address, port).sync();
			channelFuture.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					closeGracefylly();
				}

			});
		} catch (Exception e) {
			closeGracefylly();
			logger.error("start http server error.", e);
		}
	}

	private void closeGracefylly() {
		try {
			if (parentGroup != null) {
				parentGroup.shutdownGracefully();
				parentGroup.terminationFuture().sync();
			}
			if (childGroup != null) {
				childGroup.shutdownGracefully();
				childGroup.terminationFuture().sync();
			}
		} catch (Exception es) {
			logger.error("shutdown http gracefylly error.", es);
		}
	}

	public abstract void loadExecutor(AbstracteExecutor<Command> executor);
}
