package com.akaxin.common.utils;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class StringHelper {

	public static String getSubString(String str, int length) {
		int count = 0;
		int offset = 0;
		char[] c = str.toCharArray();
		int size = c.length;
		if (size >= length) {
			for (int i = 0; i < c.length; i++) {
				if (c[i] > 256) {
					offset = 2;
					count += 2;
				} else {
					offset = 1;
					count++;
				}
				if (count == length) {
					return str.substring(0, i + 1);
				}
				if ((count == length + 1 && offset == 2)) {
					return str.substring(0, i);
				}
			}
		} else {
			return str;
		}
		return "";
	}

	/**
	 * 
	 * @param messagePattern
	 *            "hello,nice {} see {}"
	 * @param objects
	 *            ["to","see"]
	 * @return hello,nice to see you
	 */
	public static String format(String messagePattern, Object... objects) {
		FormattingTuple format = MessageFormatter.arrayFormat(messagePattern, objects);
		return format.getMessage();
	}
}
