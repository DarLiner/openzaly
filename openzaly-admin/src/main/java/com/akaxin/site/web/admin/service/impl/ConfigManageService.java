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
package com.akaxin.site.web.admin.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.web.admin.service.IConfigService;
import com.akaxin.site.web.utils.ArraysUtils;

@Service
public class ConfigManageService implements IConfigService {
	private static final Logger logger = LoggerFactory.getLogger(ConfigManageService.class);

	@Override
	public Map<Integer, String> getSiteConfig() {
		Map<Integer, String> configMap = SiteConfig.getConfigMap();
		return configMap;
	}

	@Override
	public boolean updateSiteConfig(String siteUserId, Map<Integer, String> config) {
		boolean isAdmin = SiteConfig.isSiteSuperAdmin(siteUserId);
		boolean result = SiteConfigDao.getInstance().updateSiteConfig(config, isAdmin);
		SiteConfig.updateConfig();
		logger.info("siteUserId={} isAdmin={} update configMap={} result={}", siteUserId, isAdmin, config, result);
		return result;
	}

	@Override
	public boolean addUserManager(String siteUserId) {
		boolean result = false;
		String siteManagers = SiteConfigDao.getInstance().getSiteManagers();
		if (StringUtils.isNotEmpty(siteManagers)) {
			if (!siteManagers.contains(siteUserId)) {
				siteManagers = siteManagers + "," + siteUserId;
			}
		} else {
			siteManagers = siteUserId;
		}
		result = SiteConfigDao.getInstance().updateSiteManagers(siteManagers);
		SiteConfig.updateConfig();
		return result;
	}

	@Override
	public boolean deleteUserManager(String siteUserId) {
		boolean result = false;
		String siteManagers = SiteConfigDao.getInstance().getSiteManagers();
		if (StringUtils.isNotEmpty(siteManagers)) {
			String[] managers = siteManagers.split(",");
			List<String> managerList = ArraysUtils.asList(managers);
			if (managerList.contains(siteUserId)) {
				managerList.remove(siteUserId);
				String newManagers = listToString(null, ",");
				result = SiteConfigDao.getInstance().updateSiteManagers(newManagers);
				SiteConfig.updateConfig();
			}
		}
		return result;
	}

	@Override
	public boolean setUserDefaultFriends(String siteUserId) {
		boolean result = false;
		String defaultFriends = SiteConfigDao.getInstance().getDefaultUserFriends();
		if (StringUtils.isNotEmpty(defaultFriends)) {
			if (!defaultFriends.contains(siteUserId)) {
				defaultFriends = defaultFriends + "," + siteUserId;
			}
		} else {
			defaultFriends = siteUserId;
		}
		result = SiteConfigDao.getInstance().updateDefaultUserFriends(defaultFriends);
		SiteConfig.updateConfig();
		return result;
	}

	@Override
	public boolean deleteUserDefaultFriends(String siteUserId) {
		boolean result = false;
		String defaultFriends = SiteConfigDao.getInstance().getDefaultUserFriends();
		if (StringUtils.isNotEmpty(defaultFriends)) {
			String[] friends = defaultFriends.split(",");
			List<String> friendList = ArraysUtils.asList(friends);
			if (friendList.contains(siteUserId)) {
				friendList.remove(siteUserId);
				String newFriends = listToString(friendList, ",");
				result = SiteConfigDao.getInstance().updateDefaultUserFriends(newFriends);
				SiteConfig.updateConfig();
			}
		}
		return result;
	}

	@Override
	public boolean setUserDefaultGroups(String siteGroupId) {
		boolean result = false;
		String defaultGroups = SiteConfigDao.getInstance().getDefaultUserGroups();
		if (StringUtils.isNotEmpty(defaultGroups)) {
			if (!defaultGroups.contains(siteGroupId)) {
				defaultGroups = defaultGroups + "," + siteGroupId;
			}
		} else {
			defaultGroups = siteGroupId;
		}
		result = SiteConfigDao.getInstance().updateDefaultUserGroups(defaultGroups);
		SiteConfig.updateConfig();
		return result;
	}

	@Override
	public boolean deleteUserDefaultGroup(String siteGroupId) {
		boolean result = false;
		String defaultGroups = SiteConfigDao.getInstance().getDefaultUserGroups();
		if (StringUtils.isNotEmpty(defaultGroups)) {
			String[] groups = defaultGroups.split(",");
			List<String> groupList = ArraysUtils.asList(groups);
			if (groupList.contains(siteGroupId)) {
				groupList.remove(siteGroupId);
				String newGroups = listToString(groupList, ",");
				result = SiteConfigDao.getInstance().updateDefaultUserGroups(newGroups);
			}
		}
		return result;
	}

	private static String listToString(Collection<? extends String> colls, String split) {
		if (colls == null || colls.size() == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String str : colls) {
			sb.append(str);
			if (++i < colls.size()) {
				sb.append(split);
			}
		}
		return sb.toString();
	}

	@Override
	public List<String> getUserDefaultFriendList() {
		String defaultFriends = SiteConfigDao.getInstance().getDefaultUserFriends();
		if (StringUtils.isNotEmpty(defaultFriends)) {
			String[] friends = defaultFriends.split(",");
			return ArraysUtils.asList(friends);
		}
		return null;
	}

	@Override
	public List<String> getUserDefaultGroupList() {
		String defaultGroups = SiteConfigDao.getInstance().getDefaultUserGroups();
		if (StringUtils.isNotEmpty(defaultGroups)) {
			String[] groups = defaultGroups.split(",");
			return ArraysUtils.asList(groups);
		}
		return null;
	}

}
