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
package com.akaxin.site.storage.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 
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

	public Map<Integer, String> querySiteConfig() throws SQLException {
		long startTime = System.currentTimeMillis();
		Map<Integer, String> configMap = new HashMap<Integer, String>();
		String sql = "SELECT config_key,config_value FROM " + SITE_CONFIG_INFO_TABLE + " ;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			configMap.put(rs.getInt(1), rs.getString(2));
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, configMap, sql);
		return configMap;
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

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + value + "," + key);

		return result;
	}

	public int updateSiteConfig(Map<Integer, String> configMap) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);

		for (Map.Entry<Integer, String> configEntry : configMap.entrySet()) {
			preStatement.setString(1, configEntry.getValue());
			preStatement.setInt(2, configEntry.getKey());
			int updateResult = preStatement.executeUpdate();

			if (updateResult == 0) {
				updateResult = saveSiteConfig(configEntry.getKey(), configEntry.getValue());
			}

			if (updateResult > 0) {
				result++;
			}
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, configMap, sql);

		return result;
	}

	public int saveSiteConfig(int configKey, String configValue) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + SITE_CONFIG_INFO_TABLE + "(config_key,config_value) VALUES(?,?);";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, configKey);
		preStatement.setString(2, configValue);
		int addResult = preStatement.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, addResult, sql);

		return addResult;
	}
}
