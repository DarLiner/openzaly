/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
*/
package com.akaxin.site.boot.utils;

import java.net.Inet4Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取本地地址
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 11:47:32
 */
public class IPUtils {
	private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);
	private static String HOST_NAME = "localhost";
	private static String HOST_ADDRESS = "127.0.0.1";
	private static String DEFAULT_HOST_ADDRESS = "0.0.0.0";

	static {
		initHostAddress();
		initHostName();
	}

	private static void initHostName() {
		try {
			HOST_NAME = Inet4Address.getLocalHost().getHostName();
		} catch (Exception e) {
			logger.error("get hostname error.", e);
		}
	}

	private static void initHostAddress() {
		try {
			HOST_ADDRESS = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {
			logger.error("get host address error.", e);
		}
	}

	public static String getHostName() {
		return HOST_NAME;
	}

	public static String getHostAddress() {
		return HOST_ADDRESS;
	}

	public static String getDefaultHostAddress() {
		return DEFAULT_HOST_ADDRESS;
	}

}
