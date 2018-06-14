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
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.connector.exception.HttpServerException;
import com.akaxin.site.connector.http.handler.HttpServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class HttpServer {
	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private ServerBootstrap bootstrap;
	private EventLoopGroup parentGroup;
	private EventLoopGroup childGroup;
	private AbstracteExecutor<Command, CommandResponse> executor;

	public HttpServer() {
		try {
			executor = new SimpleExecutor<Command, CommandResponse>();
			loadExecutor(executor);
			int needThreadNum = Runtime.getRuntime().availableProcessors() + 1;
			int parentNum = 5;// accept from channel socket
			int childNum = needThreadNum * 2 + 5;// give to business handler
			bootstrap = new ServerBootstrap();
			parentGroup = new NioEventLoopGroup(parentNum);
			childGroup = new NioEventLoopGroup(childNum);
			bootstrap.group(parentGroup, childGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			// 接受连接的可连接队列大小
			bootstrap.option(ChannelOption.SO_BACKLOG, 120);
			bootstrap.option(ChannelOption.SO_REUSEADDR, true);
			// 设置缓存大小
			bootstrap.option(ChannelOption.SO_RCVBUF, 256 * 1024);
			bootstrap.option(ChannelOption.SO_SNDBUF, 256 * 1024);// 256 KB/字节

			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			/**
			 * 接受缓存区，动态内存分配端的算法
			 */
			bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new HttpResponseEncoder());
					ch.pipeline().addLast(new HttpRequestDecoder());
					ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
					ch.pipeline().addLast(new HttpServerHandler(executor));
				}
			});
		} catch (Exception e) {
			closeGracefylly();
			logger.error(AkxProject.PLN + " init http server error.", e);
			System.exit(-200);
		}
	}

	public void start(String address, int port) throws HttpServerException {
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
			throw new HttpServerException("start openzaly http-server error", e);
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
		} catch (InterruptedException e) {
			logger.error("shutdown http gracefylly error.", e);
		}
	}

	public abstract void loadExecutor(AbstracteExecutor<Command, CommandResponse> executor);
}
