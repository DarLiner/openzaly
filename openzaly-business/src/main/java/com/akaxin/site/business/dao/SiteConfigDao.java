/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.business.dao;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.ConfigProto.ConfigKey;
import com.akaxin.site.storage.api.ISiteConfigDao;
import com.akaxin.site.storage.service.SiteConfigDaoService;

/**
 * 站点配置，供业务逻辑使用
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 16:18:58
 */
public class SiteConfigDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteConfigDao.class);

	private SiteConfigDao() {
	}

	public static SiteConfigDao getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static SiteConfigDao instance = new SiteConfigDao();
	}

	private ISiteConfigDao siteConfigDao = new SiteConfigDaoService();

	public Map<Integer, String> getSiteConfig() {
		try {
			return siteConfigDao.getSiteConfig();
		} catch (SQLException e) {
			logger.error("get site profile error.", e);
		}
		return null;
	}

	public boolean updateSiteConfig(Map<Integer, String> configMap, boolean isAdmin) {
		int count = 0;
		try {
			if (configMap != null) {
				count = siteConfigDao.updateSiteConfig(configMap, isAdmin);
				if (count == configMap.size()) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger.error("update site configmap error.", e);
		}
		return false;
	}

	public boolean updateSiteConfig(int key, String value) {
		try {
			return siteConfigDao.updateSiteConfig(key, value) > 0;
		} catch (SQLException e) {
			logger.error("update site config error.", e);
		}
		return false;
	}

	public String getSiteManagers() {
		try {
			return siteConfigDao.getSiteConfigValue(ConfigKey.SITE_MANAGER_VALUE);
		} catch (SQLException e) {
			logger.error("get site user managers error.", e);
		}
		return null;
	}

	public boolean updateSiteManagers(String userManagers) {
		return updateSiteConfig(ConfigKey.SITE_MANAGER_VALUE, userManagers);
	}

	public String getDefaultUserFriends() {
		try {
			return siteConfigDao.getSiteConfigValue(ConfigKey.DEFAULT_USER_FRIENDS_VALUE);
		} catch (SQLException e) {
			logger.error("get site default user friends error.", e);
		}
		return null;
	}

	public boolean updateDefaultUserFriends(String defaultFriends) {
		return updateSiteConfig(ConfigKey.DEFAULT_USER_FRIENDS_VALUE, defaultFriends);
	}

	public String getDefaultUserGroups() {
		try {
			return siteConfigDao.getSiteConfigValue(ConfigKey.DEFAULT_USER_GROUPS_VALUE);
		} catch (SQLException e) {
			logger.error("get site official group default error.", e);
		}
		return null;
	}

	public boolean updateDefaultUserGroups(String defaultGroupIds) {
		return updateSiteConfig(ConfigKey.DEFAULT_USER_GROUPS_VALUE, defaultGroupIds);
	}

}
