package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ISiteConfigDao {
	public Map<Integer, String> getSiteConfig() throws SQLException;

	int updateSiteConfig(Map<Integer, String> configMap, boolean isAdmin) throws SQLException;

	int updateSiteConfig(int key, String value) throws SQLException;

    boolean setUserDefault(String site_user_id) throws SQLException;

	List<String> getUserDefault() throws SQLException;

	boolean updateUserDefault(String site_user_id) throws SQLException;

	boolean delUserDefault(String s) throws SQLException;

    List<String> getGroupDefault() throws SQLException;

    boolean updateGroupDefault(String siteGroupId) throws SQLException;

	boolean setGroupDefault(String siteGroupId) throws SQLException;

	boolean delGroupDefault(String del) throws SQLException;

}
