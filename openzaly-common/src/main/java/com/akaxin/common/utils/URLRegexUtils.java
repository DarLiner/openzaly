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
package com.akaxin.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:36:48
 */
public class URLRegexUtils {
	public static String getParam(String url, String name) {
		String reg_pattern = "(^|&|\\?)" + name + "=([^&]*)(&|$)";
		Pattern pattern = Pattern.compile(reg_pattern);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			String[] fistParam = matcher.group().split("=");
			if (fistParam != null && fistParam.length == 2) {
				return fistParam[1];
			}
		}
		return "";
	}

	public static void main(String[] args) {
		String test = URLRegexUtils.getParam("/test?siteUserId=hellosericksiekdjhfsk", "siteUserId");

		System.out.println(test);
	}
}
