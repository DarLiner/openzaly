package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.Map;

public interface ISiteConfigDao {
	Map<Integer, String> getSiteConfig() throws SQLException;

	String getSiteConfigValue(int key) throws SQLException;

	int updateSiteConfig(Map<Integer, String> configMap, boolean isAdmin) throws SQLException;

	int updateSiteConfig(int key, String value) throws SQLException;

}
