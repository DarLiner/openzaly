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
package com.akaxin.site.message.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.message.dao.SiteConfigDao;

/**
 * 管理站点配置相关信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-14 21:18:49
 */
public class SiteConfigHelper {
	private static final Logger logger = LoggerFactory.getLogger(SiteConfigHelper.class);

	private static volatile Map<Integer, String> configMap;

	private SiteConfigHelper() {
	}

	public static Map<Integer, String> getConfigMap() {
		if (configMap == null) {
			configMap = SiteConfigDao.getInstance().getSiteConfig();
		}
		return configMap;
	}

	public static Map<Integer, String> updateConfig() {
		try {
			configMap = SiteConfigDao.getInstance().getSiteConfig();
		} catch (Exception e) {
			logger.error("update im site config error.", e);
		}
		return configMap;
	}

	public static String getConfig(ConfigProto.ConfigKey configKey) {
		try {
			return getConfigMap().get(configKey.getNumber());
		} catch (Exception e) {
			logger.error("get config value error", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 站点发送PUSH的状态值
	 * 		PUSH_NO
	 * 		PUSH_HIDDEN_TEXT
	 * 		PUSH_HIDDEN_TEXT
	 * 
	 * </pre>
	 * 
	 * @return
	 */
	public static ConfigProto.PushClientStatus getPushClientStatus() {
		try {
			String status = getConfig(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS);
			if (status != null) {
				return ConfigProto.PushClientStatus.forNumber(Integer.valueOf(status));
			}
		} catch (Exception e) {
			logger.error("get push client status error.", e);
		}
		return ConfigProto.PushClientStatus.PUSH_HIDDEN_TEXT;
	}

}