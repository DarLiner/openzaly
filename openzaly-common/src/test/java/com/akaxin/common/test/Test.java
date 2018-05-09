package com.akaxin.common.test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.akaxin.common.crypto.AESCrypto;

public class Test {
	public static void main(String[] args) throws UnsupportedEncodingException {
		try {

			String password = "1";
			String content = "123";

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(256, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();

			System.out.println(enCodeFormat.length);

			byte[] result = AESCrypto.encrypt256(enCodeFormat, content.getBytes("utf8"));

			System.out.println("result=" + result.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}