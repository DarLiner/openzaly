package com.akaxin.site.web.admin.service;

import java.util.List;
import java.util.Map;

public interface IConfigService {

	Map<Integer, String> getSiteConfig();

	boolean updateSiteConfig(String siteUserId, Map<Integer, String> config);

	boolean addUserManager(String siteUserId);

	boolean deleteUserManager(String siteUserId);

	boolean setUserDefaultFriends(String siteUserId);

	boolean deleteUserDefaultFriends(String siteUserId);

	boolean setUserDefaultGroups(String siteGroupId);

	boolean deleteUserDefaultGroup(String siteGroupId);

	List<String> getUserDefaultFriendList();

	List<String> getUserDefaultGroupList();
}
