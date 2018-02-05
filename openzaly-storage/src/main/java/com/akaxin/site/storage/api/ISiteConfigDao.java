package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.Map;

public interface ISiteConfigDao {
	public Map<Integer, String> getSiteConfig() throws SQLException;

	int updateSiteConfig(Map<Integer, String> configMap) throws SQLException;

	int updateSiteConfig(int key, String value) throws SQLException;
}
