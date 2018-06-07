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
package com.akaxin.site.storage.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.akaxin.site.storage.bean.UserSessionBean;
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.11 14:43:38
 */
public class SQLiteUserSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUserSessionDao.class);
	private static final String USER_SESSION_TABLE = SQLConst.SITE_USER_SESSION;
	private static SQLiteUserSessionDao instance = new SQLiteUserSessionDao();

	public static SQLiteUserSessionDao getInstance() {
		return instance;
	}

	public boolean saveIfAbsent(UserSessionBean bean) throws SQLException {
		return update(bean) ? true : save(bean);
	}

	public boolean save(UserSessionBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_SESSION_TABLE
				+ "(site_user_id,session_id,is_online,device_id,login_time) VALUES(?,?,?,?,?);";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getSiteUserId());
		preStatement.setString(2, bean.getSessionId());
		preStatement.setBoolean(3, bean.isOnline());
		preStatement.setString(4, bean.getDeviceId());
		preStatement.setLong(5, bean.getLoginTime());
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getSessionId(), bean.isOnline(),
				bean.getDeviceId(), bean.getLoginTime());
		return result > 0;
	}

	public boolean update(UserSessionBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_SESSION_TABLE
				+ " SET session_id=?,login_time=?,is_online=? WHERE site_user_id=? AND device_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getSessionId());
		preStatement.setLong(2, bean.getLoginTime());
		preStatement.setBoolean(3, bean.isOnline());
		preStatement.setString(4, bean.getSiteUserId());
		preStatement.setString(5, bean.getDeviceId());
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSessionId(), bean.getLoginTime(), bean.isOnline(),
				bean.getSiteUserId(), bean.getDeviceId());
		return result > 0;
	}

	public boolean setOnlineSession(String siteUserId, String deviceId, boolean online) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_SESSION_TABLE + " SET is_online=? WHERE site_user_id=? AND device_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setBoolean(1, online);
		preStatement.setString(2, siteUserId);
		preStatement.setString(3, deviceId);
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, deviceId);
		return result > 0;
	}

	public boolean checkSession(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sessionDeviceId = null;
		String sql = "SELECT device_id FROM " + USER_SESSION_TABLE + " WHERE site_user_id=? AND device_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		preStatement.setString(2, deviceId);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			sessionDeviceId = rs.getString(1);
		}

		LogUtils.dbDebugLog(logger, startTime, sessionDeviceId, sql, siteUserId, deviceId);
		return sessionDeviceId != null;
	}

	public SimpleAuthBean queryAuthSession(String sessionId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,device_id FROM " + USER_SESSION_TABLE + " WHERE session_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, sessionId);
		ResultSet rs = preStatement.executeQuery();

		SimpleAuthBean authBean = null;
		if (rs.next()) {
			authBean = new SimpleAuthBean();
			authBean.setSiteUserId(rs.getString(1));
			authBean.setDeviceId(rs.getString(2));
		}

		LogUtils.dbDebugLog(logger, startTime, authBean, sql, sessionId);
		return authBean;
	}

	public List<String> queryDeviceIds(String siteUserid) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> deviceIds = new ArrayList<String>();
		String sql = "SELECT device_id FROM " + USER_SESSION_TABLE + " WHERE site_user_id=? AND is_online=1;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserid);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			deviceIds.add(rs.getString(1));
		}

		LogUtils.dbDebugLog(logger, startTime, deviceIds.size(), sql, siteUserid);
		return deviceIds;
	}

	public boolean deleteSession(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_SESSION_TABLE + " WHERE site_user_id=? AND device_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		preStatement.setString(2, deviceId);
		int result = preStatement.executeUpdate();
		
		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, deviceId);
		return result > 0;
	}
}
