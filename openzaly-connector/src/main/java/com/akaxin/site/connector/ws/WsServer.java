package com.akaxin.site.connector.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.connector.ws.handler.WsServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * WebSocket服务启动静态类
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-02 15:15:21
 */
public abstract class WsServer {
	private static Logger logger = LoggerFactory.getLogger(WsServer.class);

	private AbstracteExecutor<Command, CommandResponse> executor;
	private ServerBootstrap bootstrap;
	private EventLoopGroup parentGroup;
	private EventLoopGroup childGroup;

	public WsServer() {
		executor = new SimpleExecutor<Command, CommandResponse>();
		loadExecutor(executor);
		// 负责对外连接线程
		parentGroup = new NioEventLoopGroup();
		// 负责对内分发业务的线程
		childGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.group(parentGroup, childGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// 30秒空闲时间设置
				ch.pipeline().addLast(new IdleStateHandler(30, 0, 60));
				// HttpServerCodec：将请求和应答消息解码为HTTP消息
				ch.pipeline().addLast(new HttpServerCodec());
				// 针对大文件上传时，把 HttpMessage 和 HttpContent 聚合成一个
				// FullHttpRequest,并定义可以接受的数据大小64M(可以支持params+multipart)
				ch.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
				// 针对大文件下发，分块写数据
				ch.pipeline().addLast(new ChunkedWriteHandler());
				// WebSocket 访问地址
				// ch.pipeline().addLast(new WebSocketServerProtocolHandler("/akaxin/ws"));
				// 自定义handler
				ch.pipeline().addLast(new WsServerHandler(executor));
			}
		});

	}

	// 启动websocket服务
	public void start(String address, int port) throws Exception {
		try {
			if (bootstrap != null) {
				ChannelFuture channelFuture = bootstrap.bind(address, port).sync();
				channelFuture.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {

					@Override
					public void operationComplete(Future<? super Void> future) throws Exception {
						closeGracefully();
					}
				});
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			closeGracefully();
			throw new Exception("start websocket server error", e);
		}
	}

	private void closeGracefully() {
		try {
			if (parentGroup != null) {
				// terminate all threads
				parentGroup.shutdownGracefully();
				// wait for all threads terminated
				parentGroup.terminationFuture().sync();
			}
			if (childGroup != null) {
				// terminate all threads
				childGroup.shutdownGracefully();
				// wait for all threads terminated
				childGroup.terminationFuture().sync();
			}
		} catch (Exception es) {
			logger.error(AkxProject.PLN + " shutdown netty gracefully error.", es);
		}
	}

	public abstract void loadExecutor(AbstracteExecutor<Command, CommandResponse> executor);
}
