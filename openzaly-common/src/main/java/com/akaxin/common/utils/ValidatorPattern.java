package com.akaxin.common.utils;

import java.util.regex.Pattern;

/**
 * 正则表达式，验证 <br>
 * 1.momoid <br>
 * 2.手机号 <br>
 * 3.email <br>
 * 4.汉子 <br>
 * 5.身份证 <br>
 * 6.url <br>
 * 7.IP <br>
 * 
 * @author Sam
 * @since 2016.12.22
 */
public class ValidatorPattern {
	/**
	 * 正则表达式：验证手机号
	 */
	public static final String REGEX_PHONDID = "^((13[0-9])|(15[^4,\\D])|(16[0-9])|(18[0,5-9])|(19[0-9]))\\d{8}$";

	/**
	 * 正则表达式：验证邮箱
	 */
	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	/**
	 * 正则表达式：验证汉字
	 */
	public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

	/**
	 * 正则表达式：验证身份证
	 */
	public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

	/**
	 * 正则表达式：验证URL
	 */
	public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

	/**
	 * 正则表达式：验证IP地址
	 */
	public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

	/**
	 * 校验手机号
	 * 
	 * @param phoneId
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isPhoneId(String phoneId) {
		if (phoneId == null) {
			return false;
		}
		return Pattern.matches(REGEX_PHONDID, phoneId);
	}

	/**
	 * 校验邮箱
	 * 
	 * @param email
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isEmail(String email) {
		if (email == null) {
			return false;
		}
		return Pattern.matches(REGEX_EMAIL, email);
	}

	/**
	 * 校验汉字
	 * 
	 * @param chinese
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isChinese(String chinese) {
		if (chinese == null) {
			return false;
		}
		return Pattern.matches(REGEX_CHINESE, chinese);
	}

	/**
	 * 校验身份证
	 * 
	 * @param idCard
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIDCard(String idCard) {
		if (idCard == null) {
			return false;
		}
		return Pattern.matches(REGEX_ID_CARD, idCard);
	}

	/**
	 * 校验URL
	 * 
	 * @param url
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUrl(String url) {
		if (url == null) {
			return false;
		}
		return Pattern.matches(REGEX_URL, url);
	}

	/**
	 * 校验IP地址
	 * 
	 * @param ipAddr
	 * @return
	 */
	public static boolean isIPAddr(String ipAddr) {
		if (ipAddr == null) {
			return false;
		}
		return Pattern.matches(REGEX_IP_ADDR, ipAddr);
	}
}
