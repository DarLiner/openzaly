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
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.11 14:33:56
 */
public class SiteUserDeviceDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUserDeviceDao.class);
	private static final String USER_DEVICE_TABLE = SQLConst.SITE_USER_DEVICE;
	private static final String USER_SESSION_TABLE = SQLConst.SITE_USER_SESSION;
	private static SiteUserDeviceDao instance = new SiteUserDeviceDao();

	public static SiteUserDeviceDao getInstance() {
		return instance;
	}

	/**
	 * 通过公钥查询用户DeviceId
	 */
	public String queryDeviceIdByDevicePuk(String siteUserId, String devicePuk) throws SQLException {
		String deviceId = null;
		long startTime = System.currentTimeMillis();
		String sql = "SELECT device_id FROM " + USER_DEVICE_TABLE + " WHERE user_device_pubk=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, devicePuk);

			rs = pst.executeQuery();
			if (rs.next()) {
				deviceId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, deviceId, sql, devicePuk);
		return deviceId;
	}

	public boolean save(UserDeviceBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_DEVICE_TABLE
				+ "(site_user_id,device_id,user_device_pubk,device_name,device_ip,user_token,active_time,add_time) VALUES(?,?,?,?,?,?,?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getSiteUserId());
			ps.setString(2, bean.getDeviceId());
			ps.setString(3, bean.getUserDevicePubk());
			ps.setString(4, bean.getDeviceName());
			ps.setString(5, bean.getDeviceIp());
			ps.setString(6, bean.getUserToken());
			ps.setLong(7, bean.getActiveTime());
			ps.setLong(8, bean.getAddTime());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getDeviceId(),
				bean.getUserDevicePubk(), bean.getDeviceName(), bean.getDeviceIp(), bean.getUserToken(),
				bean.getActiveTime(), bean.getAddTime());
		return result == 1;
	}

	public boolean update(UserDeviceBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_DEVICE_TABLE
				+ " SET user_device_pubk=?,device_name=?,device_ip=?,user_token=?,active_time=? WHERE site_user_id=? AND device_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getUserDevicePubk());
			ps.setString(2, bean.getDeviceName());
			ps.setString(3, bean.getDeviceIp());
			ps.setString(4, bean.getUserToken());
			ps.setLong(5, bean.getActiveTime());
			ps.setString(6, bean.getSiteUserId());
			ps.setString(7, bean.getDeviceId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getUserDevicePubk(), bean.getDeviceName(),
				bean.getDeviceIp(), bean.getUserToken(), bean.getActiveTime(), bean.getSiteUserId(),
				bean.getDeviceId());
		return result == 1;
	}

	public boolean updateActiveTime(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_DEVICE_TABLE + " SET active_time=? WHERE site_user_id=? AND device_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setLong(1, System.currentTimeMillis());
			ps.setString(2, siteUserId);
			ps.setString(3, deviceId);
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, deviceId);
		return result == 1;
	}

	public UserDeviceBean queryDeviceDetails(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		UserDeviceBean deviceBean = new UserDeviceBean();
		String sql = "SELECT a.site_user_id,a.device_id,a.login_time,b.device_name,b.device_ip,b.active_time,b.add_time from "
				+ USER_SESSION_TABLE + " AS a LEFT JOIN " + USER_DEVICE_TABLE
				+ " AS b WHERE a.site_user_id=b.site_user_id AND a.device_id=b.device_id AND a.site_user_id=? AND a.device_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, deviceId);

			rs = pst.executeQuery();
			if (rs.next()) {
				deviceBean.setSiteUserId(rs.getString(1));
				deviceBean.setDeviceId(rs.getString(2));
				deviceBean.setLoginTime(rs.getLong(3));
				deviceBean.setDeviceName(rs.getString(4));
				deviceBean.setDeviceIp(rs.getString(5));
				deviceBean.setActiveTime(rs.getLong(6));
				deviceBean.setAddTime(rs.getLong(7));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, deviceBean.toString(), sql, siteUserId, deviceId);
		return deviceBean;
	}

	public String queryDeviceId(String site_user_id, String device_id) throws SQLException {
		String deviceId = null;
		long startTime = System.currentTimeMillis();
		String sql = "SELECT device_id FROM " + USER_DEVICE_TABLE + " WHERE site_user_id=? AND device_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, site_user_id);
			pst.setString(2, device_id);

			rs = pst.executeQuery();
			if (rs.next()) {
				deviceId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, deviceId, sql, site_user_id, device_id);
		return deviceId;
	}

	/**
	 * 最新的用户设备，用户最近一次活跃时间算
	 * 
	 * @param siteUserId
	 * @return
	 * @throws SQLException
	 */
	public UserDeviceBean queryLatestDevice(String siteUserId) throws SQLException {
		UserDeviceBean deviceBean = new UserDeviceBean();
		long startTime = System.currentTimeMillis();
		// String sql = "SELECT
		// site_user_id,device_id,user_device_pubk,device_name,max(active_time) FROM "
		// + USER_DEVICE_TABLE + " WHERE site_user_id=? LIMIT 1;";

		String sql = "SELECT a.site_user_id,a.device_id,a.user_device_pubk,a.device_name,a.active_time FROM "
				+ USER_DEVICE_TABLE + " AS a INNER JOIN (SELECT device_id,MAX(active_time) FROM " + USER_DEVICE_TABLE
				+ " WHERE site_user_id=? GROUP BY device_id) AS b ON a.device_id=b.device_id;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				deviceBean.setSiteUserId(rs.getString(1));
				deviceBean.setDeviceId(rs.getString(2));
				deviceBean.setUserDevicePubk(rs.getString(3));
				deviceBean.setDeviceName(rs.getString(4));
				deviceBean.setActiveTime(rs.getLong(5));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, deviceBean.toString(), sql, siteUserId);
		return deviceBean;
	}

	public List<UserDeviceBean> queryDeviceList(String siteUserId) throws SQLException {
		List<UserDeviceBean> devicesBean = new ArrayList<UserDeviceBean>();
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,device_id,user_device_pubk,device_name,active_time FROM " + USER_DEVICE_TABLE
				+ " WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			while (rs.next()) {
				UserDeviceBean deviceBean = new UserDeviceBean();
				deviceBean.setSiteUserId(rs.getString(1));
				deviceBean.setDeviceId(rs.getString(2));
				deviceBean.setUserDevicePubk(rs.getString(3));
				deviceBean.setDeviceName(rs.getString(4));
				deviceBean.setActiveTime(rs.getLong(5));
				devicesBean.add(deviceBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, devicesBean.size(), sql, siteUserId);
		return devicesBean;
	}

	public List<UserDeviceBean> queryActiveDeviceList(String siteUserId) throws SQLException {
		List<UserDeviceBean> devicesBean = new ArrayList<UserDeviceBean>();
		long startTime = System.currentTimeMillis();
		String sql = "SELECT a.site_user_id,a.device_id,a.login_time,b.user_device_pubk,b.device_name,b.active_time FROM "
				+ USER_SESSION_TABLE + " AS a LEFT JOIN " + USER_DEVICE_TABLE
				+ " AS b ON a.device_id=b.device_id AND a.site_user_id=b.site_user_id WHERE a.site_user_id=? ORDER BY b.active_time DESC;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			while (rs.next()) {
				UserDeviceBean deviceBean = new UserDeviceBean();
				deviceBean.setSiteUserId(rs.getString(1));
				deviceBean.setDeviceId(rs.getString(2));
				deviceBean.setLoginTime(rs.getLong(3));
				deviceBean.setUserDevicePubk(rs.getString(4));
				deviceBean.setDeviceName(rs.getString(5));
				deviceBean.setActiveTime(rs.getLong(6));
				devicesBean.add(deviceBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, devicesBean.size(), sql, siteUserId);
		return devicesBean;
	}

	public String queryUserToken(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String userToken = null;
		String sql = "SELECT user_token,max(active_time) FROM " + SQLConst.SITE_USER_DEVICE
				+ " WHERE site_user_id=? GROUP BY user_token LIMIT 1;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				userToken = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userToken, sql, siteUserId);
		return userToken;
	}

	/**
	 * 删除指定数量以外的其他设备
	 * 
	 * @param siteUserId
	 * @return
	 * @throws SQLException
	 */
	public int limitDeviceNum(String siteUserId, int limit) throws SQLException {
		return Math.max(deleteDeviceAsLimit(siteUserId, limit), deleteSessionAsLimit(siteUserId, limit));
	}

	private int deleteDeviceAsLimit(String siteUserId, int limit) throws SQLException {
		long startTime = System.currentTimeMillis();
		// String sql = "DELETE FROM " + USER_DEVICE_TABLE + " WHERE site_user_id='?'
		// ORDER BY active_time DESC LIMIT ?,10000;";
		String sql = "DELETE FROM " + USER_DEVICE_TABLE
				+ " WHERE site_user_id=? AND device_id NOT IN (SELECT d.device_id FROM (SELECT device_id FROM "
				+ USER_DEVICE_TABLE + " WHERE site_user_id=? ORDER BY active_time DESC LIMIT ?) as d)";
		int num = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setString(2, siteUserId);
			ps.setInt(3, limit);

			num = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, num, sql, siteUserId);
		return num;
	}

	private int deleteSessionAsLimit(String siteUserId, int limit) throws SQLException {
		long startTime = System.currentTimeMillis();
		// 删除site_user_session中设备
		// String sql = "DELETE FROM " + USER_SESSION_TABLE + " WHERE site_user_id='?'
		// ORDER BY login_time DESC LIMIT ?,10000;";
		String sql = "DELETE FROM " + USER_SESSION_TABLE
				+ " WHERE site_user_id=? AND device_id NOT IN (SELECT s.device_id FROM (SELECT device_id FROM "
				+ USER_DEVICE_TABLE + " WHERE site_user_id=? ORDER BY active_time DESC LIMIT ?) as s);";

		int num = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setString(2, siteUserId);
			ps.setInt(3, limit);

			num = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, num, sql, siteUserId);
		return num;
	}

	public boolean delDevice(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE from " + SQLConst.SITE_USER_DEVICE + " WHERE site_user_id=? ";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			result = ps.executeUpdate();
			if (result > 0) {
				LogUtils.dbDebugLog(logger, startTime, result, sql, true);
				return true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, false);
		return false;

	}
}
