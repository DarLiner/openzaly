package com.akaxin.common.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * 获取SSL访问使用的Context
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-20 21:04:43
 */
public class PlatformSSLContext {

	private static SslContext sslContext;

	private PlatformSSLContext() {

	}

	public static SslContext getSSLContext() {
		try {
			if (sslContext == null) {
				sslContext = SslContextBuilder.forClient().trustManager(PlatformTrustManagerFactory.INSTANCE).build();
			}
		} catch (Exception e) {
			throw new Error("Failed to initialize platform SSLContext", e);
		}

		return sslContext;
	}

}
