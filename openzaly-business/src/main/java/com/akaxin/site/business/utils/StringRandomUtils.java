package com.akaxin.site.business.utils;

import java.util.Random;

public class StringRandomUtils {

	private static final String STR_62_RANDOM = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// 随机生成N位字符串（A-Z，a-z，0-9）
	public static String generateRandomString(int length) {
		Random random = new Random();
		StringBuffer newRandomStr = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(STR_62_RANDOM.length());
			newRandomStr.append(STR_62_RANDOM.charAt(number));
		}
		return newRandomStr.toString();
	}

	public static void main(String args[]) {
		System.out.println(generateRandomString(64));
	}
}
