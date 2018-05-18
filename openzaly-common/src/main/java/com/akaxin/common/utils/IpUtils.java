package com.akaxin.common.utils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtils {
	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

	public static String getLocalAddress() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("get local address error", e);
		}

		return null;
	}

}
