package com.akaxin.common.test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * java实现AES256加密解密 依赖说明： bcprov-jdk15-133.jar：PKCS7Padding
 * javabase64-1.3.1.jar：base64 local_policy.jar 和
 * US_export_policy.jar需添加到%JAVE_HOME%\jre\lib\security中（lib中版本适合jdk1.7）
 */

public class AES256 {

	// AES_256_cbc pkcs7
	private static final String ALGORITHM = "AES/CBC/PKCS7Padding";

	// 加密
	public static byte[] AES_cbc_encrypt(byte[] srcData, byte[] key, byte[] iv)
			throws Exception, NoSuchProviderException, NoSuchPaddingException {
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
		byte[] encData = cipher.doFinal(srcData);
		return encData;
	}

	// 解密
	public static byte[] AES_cbc_decrypt(byte[] encData, byte[] key, byte[] iv)
			throws Exception, NoSuchProviderException, NoSuchPaddingException {
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
		byte[] decbbdt = cipher.doFinal(encData);
		return decbbdt;
	}

	public static void main(String[] args) throws NoSuchProviderException, NoSuchPaddingException, Exception {
		// TODO Auto-generated method stub

		byte[] key = new byte[32];
		byte[] iv = new byte[16];

		String srcStr = "This is java bcprovlib pkcs7padding PKCS7 TEST";
		System.out.println(srcStr);

		// 设置key 全8，iv，全1，这里测试用
		for (int i = 0; i < 32; i++) {
			key[i] = 8;
			if (i < 16) {
				iv[i] = 1;
			}
		}

		byte[] encbt = AES_cbc_encrypt(srcStr.getBytes(), key, iv);
		byte[] decbt = AES_cbc_decrypt(encbt, key, iv);
		String decStr = new String(decbt);
		System.out.println(decStr);

		if (srcStr.equals(decStr)) {
			System.out.println("TEST PASS");
		} else {
			System.out.println("TEST NO PASS");
		}

	}
}