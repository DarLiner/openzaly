package com.akaxin.common.crypto;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.constant.CharsetCoding;

public class AESCrypto {
	private static final Logger logger = LoggerFactory.getLogger(AESCrypto.class);
	public static final String ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 随机生成AES加密解密KEY
	 * 
	 * @return
	 */
	public static byte[] generateTSKey() {
		try {
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			SecretKey secretKey = kg.generateKey();
			return secretKey.getEncoded();
		} catch (Exception e) {
			logger.error("generate ts key error", e);
		}
		return null;
	}

	/**
	 * 通过key生成AES加密解密key
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] generateTSKey(String key) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(key.getBytes(CharsetCoding.ISO_8859_1));
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			return secretKey.getEncoded();
		} catch (Exception e) {
			logger.error("generate ts key error by key=" + key, e);
		}
		return null;
	}

	/**
	 * 加密内容
	 * 
	 * @param tsk
	 * @param content
	 * @return
	 */
	public static byte[] encrypt(byte[] tsk, byte[] content) {
		try {
			SecretKeySpec key = new SecretKeySpec(tsk, "AES");
			Cipher cipher = Cipher.getInstance(ALGORITHM);// 创建密码器
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			return cipher.doFinal(content);
		} catch (Exception e) {
			logger.error("aes encrypt error tsk-size={} content-size={}", tsk.length, content.length);
		}
		return null;
	}

	/**
	 * 解密内容
	 * 
	 * @param tsk
	 * @param content
	 * @return
	 */
	public static byte[] decrypt(byte[] tsk, byte[] content) {
		try {
			SecretKeySpec key = new SecretKeySpec(tsk, "AES");
			Cipher cipher = Cipher.getInstance(ALGORITHM);// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			return cipher.doFinal(content);
		} catch (Exception e) {
			logger.error("AES decrypt error,tsk-len={} content-len={}", tsk.length, content.length);
		}
		return null;
	}

}