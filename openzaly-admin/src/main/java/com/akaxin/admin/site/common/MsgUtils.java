package com.akaxin.admin.site.common;

import org.apache.commons.lang3.StringUtils;

/**
 * 消息工具类
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-25 11:49:21
 */
public class MsgUtils {
	public static String buildU2MsgId(String siteUserid) {
		StringBuilder sb = new StringBuilder("U2-");
		if (StringUtils.isNotEmpty(siteUserid)) {
			int len = siteUserid.length();
			sb.append(siteUserid.substring(0, len >= 8 ? 8 : len));
			sb.append("-");
		}
		sb.append(System.currentTimeMillis());
		return sb.toString();
	}
}
