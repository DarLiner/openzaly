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
package com.akaxin.site.connector.ssl;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import io.netty.util.internal.SystemPropertyUtil;

public class NettySocketSslContext {
	private static final String PROTOCOL = "TLS";

	private SSLContext serverContext;

	private NettySocketSslContext() {
		String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
		}

		try {
			//
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(SslKeyStore.asInputStream(), SslKeyStore.getKeyStorePassword());

			// Set up key manager factory to use our key store
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(keystore, SslKeyStore.getCertificatePassword());

			// Initialize the SSLContext to work with our key managers.
			serverContext = SSLContext.getInstance(PROTOCOL);
			serverContext.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			throw new Error("Failed to initialize the server-side SSLContext", e);
		}

	}

	public static NettySocketSslContext getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {
		static NettySocketSslContext instance = new NettySocketSslContext();
	}

	public SSLContext getServerContext() {
		return serverContext;
	}

}
