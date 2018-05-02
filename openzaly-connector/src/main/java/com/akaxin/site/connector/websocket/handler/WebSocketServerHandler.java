package com.akaxin.site.connector.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 接受客户端发送的消息，客户端发送的消息组装成TextWebSocketFrame格式
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-02 15:01:08
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private static Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);

	
	// 客户端连接上服务端
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ChannelId" + ctx.channel().id().asLongText());
	}

	// 从客户端读取消息
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(msg.toString());
		System.out.println(msg.text());
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		logger.error("ws channel exception happen", cause);
	}

}
