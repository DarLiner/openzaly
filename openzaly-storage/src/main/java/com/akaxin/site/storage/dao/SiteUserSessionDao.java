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

import java.sql.Connection;
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
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * session 只从主库中获取session，不做主从分离
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.11 14:43:38
 */
public class SiteUserSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUserSessionDao.class);
	private static final String USER_SESSION_TABLE = SQLConst.SITE_USER_SESSION;
	private static SiteUserSessionDao instance = new SiteUserSessionDao();

	public static SiteUserSessionDao getInstance() {
		return instance;
	}

	public boolean saveIfAbsent(UserSessionBean bean) throws SQLException {
		return update(bean) ? true : save(bean);
	}

	public boolean save(UserSessionBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_SESSION_TABLE
				+ "(site_user_id,session_id,is_online,device_id,login_time) VALUES(?,?,?,?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getSiteUserId());
			ps.setString(2, bean.getSessionId());
			ps.setBoolean(3, bean.isOnline());
			ps.setString(4, bean.getDeviceId());
			ps.setLong(5, bean.getLoginTime());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getSessionId(), bean.isOnline(),
				bean.getDeviceId(), bean.getLoginTime());
		return result > 0;
	}

	public boolean update(UserSessionBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_SESSION_TABLE
				+ " SET session_id=?,login_time=?,is_online=? WHERE site_user_id=? AND device_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getSessionId());
			ps.setLong(2, bean.getLoginTime());
			ps.setBoolean(3, bean.isOnline());
			ps.setString(4, bean.getSiteUserId());
			ps.setString(5, bean.getDeviceId());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSessionId(), bean.getLoginTime(), bean.isOnline(),
				bean.getSiteUserId(), bean.getDeviceId());
		return result > 0;
	}

	public boolean setOnlineSession(String siteUserId, String deviceId, boolean online) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_SESSION_TABLE + " SET is_online=? WHERE site_user_id=? AND device_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setBoolean(1, online);
			ps.setString(2, siteUserId);
			ps.setString(3, deviceId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, deviceId);
		return result > 0;
	}

	public boolean checkSession(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sessionDeviceId = null;
		String sql = "SELECT device_id FROM " + USER_SESSION_TABLE + " WHERE site_user_id=? AND device_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, deviceId);

			rs = pst.executeQuery();
			if (rs.next()) {
				sessionDeviceId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, sessionDeviceId, sql, siteUserId, deviceId);
		return sessionDeviceId != null;
	}

	public SimpleAuthBean queryAuthSession(String sessionId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,device_id FROM " + USER_SESSION_TABLE + " WHERE session_id=?;";

		SimpleAuthBean authBean = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, sessionId);

			rs = pst.executeQuery();
			if (rs.next()) {
				authBean = new SimpleAuthBean();
				authBean.setSiteUserId(rs.getString(1));
				authBean.setDeviceId(rs.getString(2));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, authBean, sql, sessionId);
		return authBean;
	}

	public List<String> queryDeviceIds(String siteUserid) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> deviceIds = new ArrayList<String>();
		String sql = "SELECT device_id FROM " + USER_SESSION_TABLE + " WHERE site_user_id=? AND is_online=1;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserid);

			rs = pst.executeQuery();
			while (rs.next()) {
				deviceIds.add(rs.getString(1));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, deviceIds.size(), sql, siteUserid);
		return deviceIds;
	}

	public boolean deleteSession(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_SESSION_TABLE + " WHERE site_user_id=? AND device_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setString(2, deviceId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, deviceId);
		return result > 0;
	}
}
