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
package com.akaxin.site.storage.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:12
 */
public class SQLiteSiteConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteSiteConfigDao.class);
    private static final String SITE_CONFIG_INFO_TABLE = SQLConst.SITE_CONFIG_INFO;

    private SQLiteSiteConfigDao() {
    }

    public static SQLiteSiteConfigDao getInstance() {
        return SingletonHolder.instance;
    }


    static class SingletonHolder {

        private static SQLiteSiteConfigDao instance = new SQLiteSiteConfigDao();

    }


    public boolean delGroupDefault(String del) throws SQLException {
        long startTime = System.currentTimeMillis();
        String updateSql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(updateSql);
        preparedStatement.setString(1, del);
        preparedStatement.setInt(2, ConfigProto.ConfigKey.DEFAULT_USER_GROUPS_VALUE);
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            LogUtils.dbDebugLog(logger, startTime, del, updateSql);
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, del, updateSql);
        return false;
    }

    public Map<Integer, String> querySiteConfig() throws SQLException {
        long startTime = System.currentTimeMillis();
        Map<Integer, String> configMap = new HashMap<Integer, String>();
        String sql = "SELECT config_key,config_value FROM " + SITE_CONFIG_INFO_TABLE + ";";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        ResultSet rs = preStatement.executeQuery();
        while (rs.next()) {
            int key = rs.getInt(1);
            String value = rs.getString(2);
            if (StringUtils.isNotEmpty(value)) {
                configMap.put(key, value);
            }
        }

        LogUtils.dbDebugLog(logger, startTime, configMap, sql);
        return configMap;
    }

    public boolean delUserDefault(String s) throws SQLException {
        long startTime = System.currentTimeMillis();
        String updateSql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(updateSql);
        preparedStatement.setString(1, s);
        preparedStatement.setInt(2, ConfigProto.ConfigKey.DEFAULT_USER_FRIENDS_VALUE);
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            LogUtils.dbDebugLog(logger, startTime, s, updateSql);
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, s, updateSql);

        return false;
    }

    public List<String> getGroupDefault() throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "SELECT config_value FROM " + SITE_CONFIG_INFO_TABLE + " WHERE config_key = ? ;";
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setInt(1, ConfigProto.ConfigKey.DEFAULT_USER_GROUPS_VALUE);
        ResultSet resultSet = preStatement.executeQuery();
        if (resultSet == null || resultSet.isClosed()) {
            return null;
        }
        ArrayList<String> Beans = new ArrayList<>();
        String string = resultSet.getString(1);
        if (string == null) {
            return null;
        }
        String[] split = string.split(",");
        for (String s : split) {
            Beans.add(s);
        }
        LogUtils.dbDebugLog(logger, startTime, resultSet, sql);
        return Beans;
    }

    public boolean setUserDefault(String site_user_id) throws SQLException {
        long startTime = System.currentTimeMillis();
        String firstSql = "INSERT INTO " + SITE_CONFIG_INFO_TABLE + "(config_key,config_value) VALUES(?,?);";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(firstSql);
        preparedStatement.setInt(1, ConfigProto.ConfigKey.DEFAULT_USER_FRIENDS_VALUE);
        preparedStatement.setString(2, site_user_id);
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            LogUtils.dbDebugLog(logger, startTime, site_user_id, firstSql);
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, site_user_id, firstSql);
        return false;

    }

    public boolean updateUserDefault(String site_user_id) throws SQLException {
        long startTime = System.currentTimeMillis();
        String querySql = "SELECT config_value FROM site_config_info WHERE config_key = ? ;";
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(querySql);
        preStatement.setInt(1, ConfigProto.ConfigKey.DEFAULT_USER_FRIENDS_VALUE);
        ResultSet resultSet = preStatement.executeQuery();
        if (!resultSet.next()) {
            return false;
        }
        String a = resultSet.getString(1);
        if (StringUtils.isNotEmpty(a)) {
            a = a + "," + site_user_id;
        } else {
            a = site_user_id;
        }
        String updateSql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(updateSql);
        preparedStatement.setString(1, a);
        preparedStatement.setInt(2, ConfigProto.ConfigKey.DEFAULT_USER_FRIENDS_VALUE);
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            LogUtils.dbDebugLog(logger, startTime, resultSet, updateSql);
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, resultSet, updateSql);
        return false;
    }

    public List<String> getUserDefault() throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "SELECT config_value FROM site_config_info WHERE config_key = ? ;";
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setInt(1, ConfigProto.ConfigKey.DEFAULT_USER_FRIENDS_VALUE);
        ResultSet resultSet = preStatement.executeQuery();
        if (resultSet == null || resultSet.isClosed()) {
            return null;
        }
        ArrayList<String> Beans = new ArrayList<>();
        String string = resultSet.getString(1);
        if (string == null) {
            return null;
        }
        String[] split = string.split(",");
        for (String s : split) {
            Beans.add(s);
        }
        LogUtils.dbDebugLog(logger, startTime, resultSet, sql);
        return Beans;
    }

    public int updateSiteConfig(int key, String value) throws SQLException {
        long startTime = System.currentTimeMillis();
        int result = 0;
        String sql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, value);
        preStatement.setInt(2, key);
        result = preStatement.executeUpdate();

        if (result == 0) {
            result = saveSiteConfig(key, value);
        }

        LogUtils.dbDebugLog(logger, startTime, result, sql, value, key);
        return result;
    }

    public int updateSiteConfig(Map<Integer, String> configMap, boolean isAdmin) throws SQLException {
        long startTime = System.currentTimeMillis();
        int result = 0;
        String sql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        for (Map.Entry<Integer, String> configEntry : configMap.entrySet()) {
            int configKey = configEntry.getKey();

            if (ConfigProto.ConfigKey.SITE_MANAGER_VALUE == configKey && !isAdmin) {
                continue;
            }

            preStatement.setString(1, configEntry.getValue());
            preStatement.setInt(2, configEntry.getKey());
            int updateResult = preStatement.executeUpdate();

            if (updateResult == 0) {
                updateResult = saveSiteConfig(configEntry.getKey(), configEntry.getValue());
            }

            if (updateResult > 0) {
                result++;
            }

            LogUtils.dbDebugLog(logger, startTime, configMap, sql, configEntry.getValue(), configEntry.getKey());
        }

        LogUtils.dbDebugLog(logger, startTime, result, sql);
        return result;
    }

    public int saveSiteConfig(int configKey, String configValue) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO " + SITE_CONFIG_INFO_TABLE + "(config_key,config_value) VALUES(?,?);";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setInt(1, configKey);
        preStatement.setString(2, configValue);
        int result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, configKey, configValue);
        return result;
    }

    public boolean updateGroupDefault(String siteGroupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String querySql = "SELECT config_value FROM " + SITE_CONFIG_INFO_TABLE + " WHERE config_key = ? ;";
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(querySql);
        preStatement.setInt(1, ConfigProto.ConfigKey.DEFAULT_USER_GROUPS_VALUE);
        ResultSet resultSet = preStatement.executeQuery();
        if (!resultSet.next()) {
            return false;
        }
        String a = resultSet.getString(1);
        if (StringUtils.isNotEmpty(a)) {
            a = a + "," + siteGroupId;
        } else {
            a = siteGroupId;
        }
        String updateSql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(updateSql);
        preparedStatement.setString(1, a);
        preparedStatement.setInt(2, ConfigProto.ConfigKey.DEFAULT_USER_GROUPS_VALUE);
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            LogUtils.dbDebugLog(logger, startTime, resultSet, updateSql);
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, resultSet, updateSql);
        return false;
    }

    public boolean setGroupDefault(String siteGroupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String firstSql = "INSERT INTO " + SITE_CONFIG_INFO_TABLE + "(config_key,config_value) VALUES(?,?);";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(firstSql);
        preparedStatement.setInt(1, ConfigProto.ConfigKey.DEFAULT_USER_GROUPS_VALUE);
        preparedStatement.setString(2, siteGroupId);
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            LogUtils.dbDebugLog(logger, startTime, siteGroupId, firstSql);
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, siteGroupId, firstSql);
        return false;

    }
}
