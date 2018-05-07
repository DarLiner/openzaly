package com.akaxin.site.business.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 缓存websession，过期1分钟
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-07 19:25:38
 */
public class WebSessionCache {
	private static final Logger logger = LoggerFactory.getLogger(WebSessionCache.class);

	/**
	 * 最大数量1000个 最长过期时间1分钟
	 *
	 */
	private static Cache<String, String> webSessCache = CacheBuilder.newBuilder().maximumSize(1000)
			.expireAfterWrite(60, TimeUnit.SECONDS).build();

	public static String getSiteUserId(String sessionId) {
		String siteUserId = webSessCache.getIfPresent(sessionId);
		if (StringUtils.isNotEmpty(siteUserId)) {
			webSessCache.invalidate(sessionId);
		}
		return siteUserId;
	}

	public static void putWebAuthSession(String sessionId, String siteUserId) {
		webSessCache.put(sessionId, siteUserId);
	}

	public static void main(String[] args) {
		webSessCache.put("101", "hello world!");

		String hello = getSiteUserId("101");
		System.out.println("hello = " + hello);

		String hello2 = getSiteUserId("101");
		System.out.println("hello = " + hello2);

		String hello3 = getSiteUserId("1012");
		System.out.println("hello = " + hello3);

	}

}
