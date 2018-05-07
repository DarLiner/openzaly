package com.akaxin.site.storage.api;

import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ISiteConfigDao {
	public Map<Integer, String> getSiteConfig() throws SQLException;

	int updateSiteConfig(Map<Integer, String> configMap, boolean isAdmin) throws SQLException;

	int updateSiteConfig(int key, String value) throws SQLException;

    boolean setUserDefault(String site_user_id) throws SQLException;

	List<UserProfileBean> getUserDefault() throws SQLException;

	boolean updateUserDefault(String site_user_id) throws SQLException;
}
