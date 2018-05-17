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
package com.akaxin.common.crypto;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSACrypto {
	private static final Logger logger = LoggerFactory.getLogger(RSACrypto.class);
	private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

	// 生成keypair
	public static KeyPair buildRSAKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024);
		return keyPairGenerator.genKeyPair();
	}

	public static byte[] encrypt(Key key, String content) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(content.getBytes());
	}

	public static byte[] encrypt(Key key, byte[] content) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(content);
	}

	public static byte[] decrypt(Key key, byte[] encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(encrypted);
	}

	/**
	 * RSA转换成PEM格式
	 * 
	 * @param key
	 * @return
	 */
	public static String getPEMFromRSAKey(Key key) {
		StringWriter pemStrWriter = new StringWriter();
		PEMWriter pemWriter = new PEMWriter(pemStrWriter);
		try {
			pemWriter.writeObject(key);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				if (pemWriter != null) {
					pemWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return pemStrWriter.toString();
	}

	public static PrivateKey getRSAPriKeyFromPem(String pem) {
		PEMReader pemReader = new PEMReader(new StringReader(pem));
		PrivateKey key = null;
		try {
			Security.addProvider(new BouncyCastleProvider());
			key = (PrivateKey) pemReader.readObject();
		} catch (Exception e) {
			logger.error("get RSA private key from pem error.", e);
		} finally {
			try {
				if (pemReader != null) {
					pemReader.close();
				}
			} catch (Exception ec) {
				logger.error("cloase pem reader error.", ec);
			}
		}
		return key;
	}

	public static PublicKey getRSAPubKeyFromPem(String pem) {
		PEMReader pemReader = new PEMReader(new StringReader(pem));
		PublicKey key = null;
		try {
			Security.addProvider(new BouncyCastleProvider());
			key = (PublicKey) pemReader.readObject();
		} catch (Exception e) {
			logger.error("get RSA public key from pem error.", e);
		} finally {
			try {
				if (pemReader != null) {
					pemReader.close();
				}
			} catch (Exception ec) {
				logger.error("close pem reader error.", ec);
			}
		}
		return key;
	}

}
