package com.akaxin.common.utils;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringHelper {
	private static final Logger logger = LoggerFactory.getLogger(StringHelper.class);

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

	private static final String STR_16_RANDOM = "1234567890";

	// 随机生成N位字符串（A-Z，a-z，0-9）
	public static String generateRandomNumber(int length) {
		Random random = new Random();
		StringBuffer newRandomStr = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(STR_16_RANDOM.length());
			newRandomStr.append(STR_16_RANDOM.charAt(number));
		}
		return newRandomStr.toString();
	}

	public static String toLatinPinYin(String text) {
		// format for string
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

		char[] input = StringUtils.trimToEmpty(text).toCharArray();
		StringBuffer result = new StringBuffer();
		try {
			for (char ch : input) {
				if (Character.toString(ch).matches("[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(ch, format);
					result.append(temp[0]);
				} else
					result.append(Character.toString(ch));
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			logger.error("string to latin pinyin error", e);
		}
		return result.toString();
	}
}
