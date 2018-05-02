package com.akaxin.site.connector.websocket.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

/**
 * 接受客户端发送的消息，客户端发送的消息组装成TextWebSocketFrame格式
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-02 15:01:08
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
	private static Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);

	private static final String AKAXIN_WS_PATH = "/akaxin/ws";
	// ws 握手
	private WebSocketServerHandshaker wsHandshaker;

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
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			// http 请求握手
			doHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			// websocket 请求
			doWSRequest(ctx, (WebSocketFrame) msg);
		} else {
			// 错误请求，关闭连接
			ctx.close();
		}

	}

	// 处理http请求，ws握手使用http请求
	private void doHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
		if (request.decoderResult().isFailure()) {
			return;
		}

		System.out.println("http request method=" + request.method());

		String wsUrl = "ws://" + request.headers().get(HttpHeaderNames.HOST) + AKAXIN_WS_PATH;

		System.out.println("http request wsUrl=" + wsUrl);

		WebSocketServerHandshakerFactory webSocketFactory = new WebSocketServerHandshakerFactory(wsUrl, null, true);
		wsHandshaker = webSocketFactory.newHandshaker(request);
		if (wsHandshaker != null) {
			//
			ChannelFuture channelFuture = wsHandshaker.handshake(ctx.channel(), request);
			if (channelFuture.isSuccess()) {
				// 握手之后，验证用户
				System.out.println("client handshaker success");
			}
		} else {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}

	}

	// 处理ws请求
	private void doWSRequest(ChannelHandlerContext ctx, WebSocketFrame wsFrame) {
		if (wsFrame instanceof TextWebSocketFrame) {
			TextWebSocketFrame textWsFrame = (TextWebSocketFrame) wsFrame;
			System.out.println("======" + ctx.channel().remoteAddress().toString());
			System.out.println("------" + textWsFrame.text());

			TextWebSocketFrame resFrame = new TextWebSocketFrame(
					new Date().toString() + ctx.channel().id() + "：" + textWsFrame.text());
			ctx.channel().writeAndFlush(resFrame);
		} else if (wsFrame instanceof PingWebSocketFrame) {
			// ping/pong
			ctx.channel().writeAndFlush(new PongWebSocketFrame(wsFrame.content().retain()));
		} else if (wsFrame instanceof CloseWebSocketFrame) {
			// close channel
			wsHandshaker.close(ctx.channel(), (CloseWebSocketFrame) wsFrame.retain());
		}
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
