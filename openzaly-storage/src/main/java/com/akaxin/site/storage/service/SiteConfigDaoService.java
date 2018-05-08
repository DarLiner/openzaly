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
package com.akaxin.site.storage.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.akaxin.site.storage.api.ISiteConfigDao;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.sqlite.SQLiteSiteConfigDao;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;

/**
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

    @Override
    public boolean setUserDefault(String site_user_id) throws SQLException {
        return SQLiteSiteConfigDao.getInstance().setUserDefault(site_user_id);
    }

    @Override
    public List<String> getUserDefault() throws SQLException {
        return SQLiteSiteConfigDao.getInstance().getUserDefault();
    }

    @Override
    public boolean updateUserDefault(String site_user_id) throws SQLException {
        return SQLiteSiteConfigDao.getInstance().updateUserDefault(site_user_id);
    }

    @Override
    public boolean delUserDefault(String s) throws SQLException {
        return SQLiteSiteConfigDao.getInstance().delUserDefault(s);
    }

    @Override
    public List<String> getGroupDefault() throws SQLException {
        return SQLiteSiteConfigDao.getInstance().getGroupDefault();
    }

    @Override
    public boolean updateGroupDefault(String siteGroupId) throws SQLException {
        return SQLiteSiteConfigDao.getInstance().updateGroupDefault(siteGroupId);
    }

    @Override
    public boolean setGroupDefault(String siteGroupId) throws SQLException {
        return SQLiteSiteConfigDao.getInstance().setGroupDefault(siteGroupId);
    }

    @Override
    public boolean delGroupDefault(String del) throws SQLException {
        return SQLiteSiteConfigDao.getInstance().delGroupDefault(del);
    }

}