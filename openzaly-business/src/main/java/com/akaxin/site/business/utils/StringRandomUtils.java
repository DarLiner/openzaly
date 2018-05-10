package com.akaxin.site.business.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class StringRandomUtils {
	
	private static final String STR_62_RANDOM = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// 随机生成N位字符串（A-Z，a-z，0-9）
	public static String generateRandomString(int length) throws NoSuchAlgorithmException {
		SecureRandom sRandom = SecureRandom.getInstance("SHA1PRNG");
		StringBuffer newRandomStr = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			int number = sRandom.nextInt(STR_62_RANDOM.length());
			newRandomStr.append(STR_62_RANDOM.charAt(number));
		}
		return newRandomStr.toString();
	}

	public static void main(String args[]) {
//		try {
//			System.out.println(generateRandomString(16));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
