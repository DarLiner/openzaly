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
package com.akaxin.site.boot.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.boot.utils.PropertiesUtils;

public class ConfigHelper implements ConfigKey {
	private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);
	private static final String CONFIG_PROPERTIES = "openzaly.properties";
	private static Properties prop;

	private ConfigHelper() {

	}

	public static Properties getProperties() {
		if (prop == null) {
			try {
				prop = PropertiesUtils.getProperties(CONFIG_PROPERTIES);
			} catch (IOException e) {
				logger.error("get config properties error,System.exit", e);
				System.exit(-1);
			}
		}
		return prop;
	}

	/**
	 * 获取服务启动时设置的配置参数，如果启动未设置，则通过配置文件获取默认的值
	 * 
	 * @param configName
	 * @return
	 */
	public static String getStringConfig(String configName) {
		String configValue = System.getProperty(configName);
		if (StringUtils.isBlank(configValue)) {
			return getProperties().get(configName).toString();
		}
		return configValue;
	}

	public static int getIntConfig(String configName) {
		String configValue = System.getProperty(configName);
		if (StringUtils.isBlank(configValue)) {
			configValue = getProperties().get(configName).toString();
		}
		return Integer.parseInt(configValue);
	}

	public static Map<Integer, String> getConfigMap() {
		Map<Integer, String> configMap = new HashMap<Integer, String>();
		configMap.put(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE, getStringConfig(SITE_ADDRESS));
		configMap.put(ConfigProto.ConfigKey.SITE_PORT_VALUE, getStringConfig(SITE_PORT));
		configMap.put(ConfigProto.ConfigKey.SITE_HTTP_ADDRESS_VALUE, getStringConfig(HTTP_ADDRESS));
		configMap.put(ConfigProto.ConfigKey.SITE_HTTP_PORT_VALUE, getStringConfig(HTTP_PORT));
		configMap.put(ConfigProto.ConfigKey.PIC_PATH_VALUE, getStringConfig(SITE_BASE_DIR));
		configMap.put(ConfigProto.ConfigKey.DB_PATH_VALUE, getStringConfig(SITE_BASE_DIR));
		configMap.put(ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE,
				ConfigProto.U2EncryptionStatus.U2_OPEN_VALUE + "");
		configMap.put(ConfigProto.ConfigKey.REGISTER_WAY_VALUE, ConfigProto.RegisterWay.USERUIC_VALUE + "");
		configMap.put(ConfigProto.ConfigKey.SITE_ADMIN_VALUE, getStringConfig(SITE_ADMINISTRATORS));
		configMap.put(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE, getStringConfig(GROUP_MEMBERS_COUNT));
		return configMap;
	}
}
