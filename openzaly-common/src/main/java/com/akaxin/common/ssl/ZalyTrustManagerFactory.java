package com.akaxin.common.ssl;

import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.internal.EmptyArrays;

/**
 * 增加netty验证ssl服务器域名
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-21 10:58:16
 */
public class ZalyTrustManagerFactory extends SimpleTrustManagerFactory {
	private static final Logger logger = LoggerFactory.getLogger(ZalyTrustManagerFactory.class);

	public static final ZalyTrustManagerFactory INSTANCE = new ZalyTrustManagerFactory();

	private static X509ExtendedTrustManager trustManager = new X509ExtendedTrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return EmptyArrays.EMPTY_X509_CERTIFICATES;
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String s) {
			logger.debug("check ssl client trusted certificate: {}", chain[0].getSubjectDN());
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
			logger.debug("check ssl server trusted certificate : {}", chain[0].getSubjectDN());
		}

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
			logger.debug("check ssl client trusted certificate: {} {} socket={}", arg0[0].getSubjectDN(), arg1,
					arg2.getLocalAddress());
		}

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
				throws CertificateException {
			logger.debug("check ssl client trusted certificate: {} {} sslengine={}", arg0[0].getSubjectDN(), arg1,
					arg2.getPeerHost());

		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
			logger.debug("check ssl server trusted certificate : {} {} socket={} ", arg0[0].getSubjectDN(), arg1,
					arg2.getLocalAddress());
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
				throws CertificateException {
			logger.debug("check ssl server trusted certificate : {} {} sslEngine={} ", arg0[0].getSubjectDN(), arg1,
					arg2.toString());

			String peerHost = arg2.getPeerHost();
			Principal pri = arg0[0].getSubjectDN();
			String serverHost = pri.getName().substring(3);
			if (StringUtils.isEmpty(serverHost) || !serverHost.equals(peerHost)) {
				throw new CertificateException("untrust server host : " + serverHost);
			}

		}
	};

	private ZalyTrustManagerFactory() {
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
