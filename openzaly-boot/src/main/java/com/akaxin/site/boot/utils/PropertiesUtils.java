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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单的加载properties文件内容
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-24 19:45:17
 */
public class PropertiesUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	public static Properties getDefaultProperties() {
		Properties properties = new Properties();
		// properties.put("site.project.env", "DEBUG");
		properties.put("site.project.env", "ONLINE");
		properties.put("site.version", "0.3.2");
		properties.put("site.address", "0.0.0.0");
		properties.put("site.port", "2021");
		properties.put("http.address", "0.0.0.0");
		properties.put("http.port", "8080");
		properties.put("site.admin.address", "127.0.0.1");
		properties.put("site.admin.port", "8081");
		properties.put("site.admin.uic", "000000");
		properties.put("site.baseDir", "./");
		properties.put("group.members.count", "100");
		return properties;
	}

	public static Properties getProperties(String configPath) throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = ClassLoader.getSystemResourceAsStream(configPath);
			properties.load(inputStream);
			return properties;
		} catch (Exception e) {
			logger.error("get properties error", e);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return properties;
	}

}
