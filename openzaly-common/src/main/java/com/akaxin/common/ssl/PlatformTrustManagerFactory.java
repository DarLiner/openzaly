package com.akaxin.common.ssl;

import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.internal.EmptyArrays;

/**
 * 使用SSL访问平台，检测证书中服务器域名是否合法
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-20 21:14:58
 */
public class PlatformTrustManagerFactory extends SimpleTrustManagerFactory {
	private static final Logger logger = LoggerFactory.getLogger(PlatformTrustManagerFactory.class);

	public static final PlatformTrustManagerFactory INSTANCE = new PlatformTrustManagerFactory();

	private static TrustManager trustManager = new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String s) {
			logger.info("Accepting a zaly client certificate: " + chain[0].getSubjectDN());
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
			Principal pri = chain[0].getSubjectDN();
			String serverHost = pri.getName().substring(3);
			if (!"push.akaxin.com".equals(serverHost) && !"platform.akaxin.com".equals(serverHost)) {
				throw new CertificateException("untrust server host : " + serverHost);
			}
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return EmptyArrays.EMPTY_X509_CERTIFICATES;
		}
	};

	private PlatformTrustManagerFactory() {
	}

	@Override
	protected void engineInit(KeyStore keyStore) throws Exception {
	}

	@Override
	protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
	}

	@Override
	protected TrustManager[] engineGetTrustManagers() {
		return new TrustManager[] { trustManager };
	}

}
