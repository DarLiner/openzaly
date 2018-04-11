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
package com.akaxin.site.storage.service;

import java.sql.SQLException;
import java.util.Map;

import com.akaxin.site.storage.api.ISiteConfigDao;
import com.akaxin.site.storage.sqlite.SQLiteSiteConfigDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:22
 */
public class SiteConfigDaoService implements ISiteConfigDao {

	@Override
	public int updateSiteConfig(Map<Integer, String> configMap, boolean isAdmin) throws SQLException {
		return SQLiteSiteConfigDao.getInstance().updateSiteConfig(configMap, isAdmin);
	}

	@Override
	public Map<Integer, String> getSiteConfig() throws SQLException {
		return SQLiteSiteConfigDao.getInstance().querySiteConfig();
	}

	@Override
	public int updateSiteConfig(int key, String value) throws SQLException {
		return SQLiteSiteConfigDao.getInstance().updateSiteConfig(key, value);
	}

}