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
package com.akaxin.site.connector.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.utils.StringHelper;
import com.akaxin.site.connector.constant.AkxProject;
import com.akaxin.site.storage.api.IPluginDao;
import com.akaxin.site.storage.bean.PluginBean;
import com.akaxin.site.storage.service.PluginServiceDao;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-20 18:41:59
 */
public class PluginSession {
	private static final Logger logger = LoggerFactory.getLogger(PluginSession.class);

	private IPluginDao pluginDao = new PluginServiceDao();

	public static PluginSession getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static PluginSession instance = new PluginSession();
	}

	public PluginBean getPlugin(String sitePluginId) {
		try {
			int pluginId = Integer.valueOf(sitePluginId);
			return pluginDao.getPluginProfile(pluginId);
		} catch (Exception e) {
			logger.error(StringHelper.format("{} get plugin error pluginId={}", AkxProject.PLN, sitePluginId), e);
		}
		return null;
	}

	public String getPluginAuthKey(String sitePluginId) {
		try {
			int pluginId = Integer.valueOf(sitePluginId);
			PluginBean bean = pluginDao.getPluginProfile(pluginId);
			if (bean != null) {
				return bean.getAuthKey();
			}
		} catch (Exception e) {
			logger.error(StringHelper.format("{} get plugin authKey error pluginId={}", AkxProject.PLN, sitePluginId),
					e);
		}
		return null;
	}
}
