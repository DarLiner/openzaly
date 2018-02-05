package com.akaxin.site.connector.utils;

public class HexUtils {

	public static String printHexString(byte[] b) {
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			returnValue.append(hex.toUpperCase() + " ");
		}

		return "[" + returnValue.toString() + "]";
	}
}
