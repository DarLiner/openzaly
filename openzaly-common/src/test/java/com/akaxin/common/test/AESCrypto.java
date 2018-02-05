package com.akaxin.common.test;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto {
	public static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

	public static byte[] encrypt(String content, String password) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者

		kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
																// 128位的key生产者
		// 加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行

		SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥

		byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
														// null。

		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥

		Cipher cipher = Cipher.getInstance("AES");// 创建密码器

		byte[] byteContent = content.getBytes("utf-8");

		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

		byte[] result = cipher.doFinal(byteContent);// 加密

		return result;

	}

	public static byte[] decrypt(byte[] content, String password) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
		kgen.init(128, new SecureRandom(password.getBytes()));
		SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
		byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
		Cipher cipher = Cipher.getInstance("AES");// 创建密码器
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
		byte[] result = cipher.doFinal(content);
		return result; // 明文
	}
}
