package com.akaxin.site.connector.ws.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.WebChannelManager;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CharsetCoding;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.site.business.cache.WebSessionCache;
import com.akaxin.site.connector.codec.parser.ChannelConst;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
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
public class WsServerHandler extends SimpleChannelInboundHandler<Object> {
	private static Logger logger = LoggerFactory.getLogger(WsServerHandler.class);

	private static final String AKAXIN_WS_PATH = "/akaxin/ws";
	// ws 握手
	private WebSocketServerHandshaker wsHandshaker;

	private AbstracteExecutor<Command, CommandResponse> executor;

	public WsServerHandler(AbstracteExecutor<Command, CommandResponse> executor) {
		this.executor = executor;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ChannelId" + ctx.channel().id().asLongText());
	}

	// 客户端连接上服务端
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel active clientIp" + ctx.channel().remoteAddress().toString());
		ctx.channel().attr(ChannelConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));
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
			ctx.close();
			return;
		}

		// 握手使用get方法，所以我们控制只接受get方法
		if (HttpMethod.GET != request.method()) {
			ctx.close();
			return;
		}

		String wsUrl = "ws://" + request.headers().get(HttpHeaderNames.HOST) + AKAXIN_WS_PATH;

		WebSocketServerHandshakerFactory webSocketFactory = new WebSocketServerHandshakerFactory(wsUrl, null, true);
		wsHandshaker = webSocketFactory.newHandshaker(request);
		if (wsHandshaker != null) {
			//
			ChannelFuture channelFuture = wsHandshaker.handshake(ctx.channel(), request);
			if (channelFuture.isSuccess()) {
				// 握手并且验证用户webSessionId
				QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
				List<String> sessionIds = queryDecoder.parameters().get("sessionId");
				if (sessionIds != null && sessionIds.size() > 0) {
					String sessionId = sessionIds.get(0);
					String siteUserId = WebSessionCache.getSiteUserId(sessionId);
					// test siteUserId
					siteUserId = "77151873-0fc7-4cf1-8bd6-67d00190fcf6";
					if (StringUtils.isNotBlank(siteUserId)) {
						ChannelSession channelSession = ctx.channel().attr(ChannelConst.CHANNELSESSION).get();
						// siteUserId && sessionId 放入Channel缓存中
						channelSession.setUserId(siteUserId);
						WebChannelManager.addChannelSession(siteUserId, channelSession);
					} else {
						// cant get authed message ,so close the channel
						// ctx.close();
					}
				} else {
					ctx.close();
				}
				System.out.println("client handshaker success parm=" + queryDecoder.parameters());

			}
		} else {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}

	}

	// 处理ws请求
	private void doWSRequest(ChannelHandlerContext ctx, WebSocketFrame wsFrame) {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = socketAddress.getAddress().getHostAddress();
		ChannelSession channelSession = ctx.channel().attr(ChannelConst.CHANNELSESSION).get();

		Command command = new Command();
		command.setSiteUserId(channelSession.getUserId());
		command.setClientIp(clientIp);
		command.setStartTime(System.currentTimeMillis());

		if (wsFrame instanceof TextWebSocketFrame) {
			TextWebSocketFrame textWsFrame = (TextWebSocketFrame) wsFrame;
			String webText = textWsFrame.text();
			try {
				command.setParams(webText.getBytes(CharsetCoding.UTF_8));
			} catch (UnsupportedEncodingException e) {
				logger.error("web message text=" + webText + " Charset code error");
			}
			TextWebSocketFrame resFrame = new TextWebSocketFrame(textWsFrame.text());
			ctx.channel().writeAndFlush(resFrame);

			executor.execute("WS-ACTION", command);
		} else if (wsFrame instanceof PingWebSocketFrame) {
			// ping/pong
			ctx.channel().writeAndFlush(new PongWebSocketFrame(wsFrame.content().retain()));
			logger.info("ws client siteUserId={} ping to server", command.getSiteUserId());
		} else if (wsFrame instanceof CloseWebSocketFrame) {
			// close channel
			wsHandshaker.close(ctx.channel(), (CloseWebSocketFrame) wsFrame.retain());
			WebChannelManager.delChannelSession(command.getSiteUserId());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ChannelSession channelSession = ctx.channel().attr(ChannelConst.CHANNELSESSION).get();
		WebChannelManager.delChannelSession(channelSession.getUserId());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// ctx.close();
		logger.error("ws channel exception happen", cause);
	}

}
