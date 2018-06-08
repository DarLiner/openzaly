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
package com.akaxin.site.storage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:12
 */
public class SiteConfigDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteConfigDao.class);
	private static final String SITE_CONFIG_INFO_TABLE = SQLConst.SITE_CONFIG_INFO;

	private SiteConfigDao() {
	}

	public static SiteConfigDao getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static SiteConfigDao instance = new SiteConfigDao();
	}

	public int saveConfig(int configKey, String configValue) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + SITE_CONFIG_INFO_TABLE + "(config_key,config_value) VALUES(?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, configKey);
			ps.setString(2, configValue);

			result = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, configKey, configValue);
		return result;
	}

	public Map<Integer, String> queryConfig() throws SQLException {
		long startTime = System.currentTimeMillis();
		Map<Integer, String> configMap = new HashMap<Integer, String>();
		String sql = "SELECT config_key,config_value FROM " + SITE_CONFIG_INFO_TABLE + ";";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {
				int key = rs.getInt(1);
				String value = rs.getString(2);
				if (StringUtils.isNotEmpty(value)) {
					configMap.put(key, value);
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, configMap, sql);
		return configMap;
	}

	public int updateConfig(int key, String value) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			ps.setInt(2, key);

			result = ps.executeUpdate();
			if (result == 0) {
				result = saveConfig(key, value);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, value, key);
		return result;
	}

	public int updateConfig(Map<Integer, String> configMap, boolean isAdmin) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + SITE_CONFIG_INFO_TABLE + " set config_value=? WHERE config_key=?;";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			for (Map.Entry<Integer, String> configEntry : configMap.entrySet()) {
				int configKey = configEntry.getKey();

				if (ConfigProto.ConfigKey.SITE_MANAGER_VALUE == configKey && !isAdmin) {
					continue;
				}

				ps.setString(1, configEntry.getValue());
				ps.setInt(2, configEntry.getKey());
				int updateResult = ps.executeUpdate();

				ps.clearParameters();
				ps.close();

				if (updateResult == 0) {
					updateResult = saveConfig(configEntry.getKey(), configEntry.getValue());
				}

				if (updateResult > 0) {
					result++;
				}

				LogUtils.dbDebugLog(logger, startTime, configMap, sql, configEntry.getValue(), configEntry.getKey());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql);
		return result;
	}

}
