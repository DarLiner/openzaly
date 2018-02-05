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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.11 14:33:56
 */
public class SQLiteUserDeviceDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUserDeviceDao.class);
	private static final String USER_DEVICE_TABLE = SQLConst.SITE_USER_DEVICE;
	private static final String USER_SESSION_TABLE = SQLConst.SITE_USER_SESSION;
	private static SQLiteUserDeviceDao instance = new SQLiteUserDeviceDao();

	public static SQLiteUserDeviceDao getInstance() {
		return instance;
	}

	/**
	 * 通过公钥查询用户DeviceId
	 */
	public String queryDeviceIdByDevicePuk(String siteUserId, String devicePuk) throws SQLException {
		String deviceId = null;
		long startTime = System.currentTimeMillis();
		String sql = "SELECT device_id FROM " + USER_DEVICE_TABLE + " WHERE user_device_pubk=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, devicePuk);

		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			deviceId = rs.getString(1);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, deviceId, sql + " = " + devicePuk);
		return deviceId;
	}

	public boolean save(UserDeviceBean bean) throws SQLException {
		boolean saveResult = false;
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_DEVICE_TABLE
				+ "(site_user_id,device_id,user_device_pubk,device_name,device_ip,user_token,active_time,add_time) VALUES(?,?,?,?,?,?,?,?);";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getSiteUserId());
		preStatement.setString(2, bean.getDeviceId());
		preStatement.setString(3, bean.getUserDevicePubk());
		preStatement.setString(4, bean.getDeviceName());
		preStatement.setString(5, bean.getDeviceIp());
		preStatement.setString(6, bean.getUserToken());
		preStatement.setLong(7, bean.getActiveTime());
		preStatement.setLong(8, bean.getAddTime());

		int result = preStatement.executeUpdate();

		if (result == 1) {
			saveResult = true;
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, String.valueOf(saveResult), sql + bean.toString());

		return saveResult;
	}

	public boolean update(UserDeviceBean bean) throws SQLException {
		boolean updateResult = false;
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_DEVICE_TABLE
				+ " SET user_device_pubk=?,device_name=?,device_ip=?,user_token=?,active_time=? WHERE site_user_id=? AND device_id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getUserDevicePubk());
		preStatement.setString(2, bean.getDeviceName());
		preStatement.setString(3, bean.getDeviceIp());
		preStatement.setString(4, bean.getUserToken());
		preStatement.setLong(5, bean.getActiveTime());
		preStatement.setString(6, bean.getSiteUserId());
		preStatement.setString(7, bean.getDeviceId());

		int result = preStatement.executeUpdate();

		if (result > 0) {
			updateResult = true;
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, updateResult, sql + bean.toString());

		return updateResult;
	}

	public boolean updateActiveTime(String siteUserId, String deviceId) throws SQLException {
		boolean updateResult = false;
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_DEVICE_TABLE + " SET active_time=? WHERE site_user_id=? AND device_id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setLong(1, System.currentTimeMillis());
		preStatement.setString(2, siteUserId);
		preStatement.setString(3, deviceId);

		int result = preStatement.executeUpdate();

		if (result > 0) {
			updateResult = true;
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, updateResult, sql + siteUserId + "," + deviceId);

		return updateResult;
	}

	public UserDeviceBean queryDeviceDetails(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		UserDeviceBean deviceBean = new UserDeviceBean();
		String sql = "SELECT a.site_user_id,a.device_id,a.login_time,b.device_name,b.device_ip,b.active_time,b.add_time from "
				+ USER_SESSION_TABLE + " AS a LEFT JOIN " + USER_DEVICE_TABLE
				+ " AS b WHERE a.site_user_id=b.site_user_id AND a.device_id=b.device_id AND a.site_user_id=? AND a.device_id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		preStatement.setString(2, deviceId);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			deviceBean.setSiteUserId(rs.getString(1));
			deviceBean.setDeviceId(rs.getString(2));
			deviceBean.setLoginTime(rs.getLong(3));
			deviceBean.setDeviceName(rs.getString(4));
			deviceBean.setDeviceIp(rs.getString(5));
			deviceBean.setActiveTime(rs.getLong(6));
			deviceBean.setAddTime(rs.getLong(7));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, deviceBean.toString(),
				sql + " = " + siteUserId + "," + deviceId);
		return deviceBean;
	}

	public String queryDeviceId(String site_user_id, String device_id) throws SQLException {
		String deviceId = null;
		long startTime = System.currentTimeMillis();
		String sql = "SELECT device_id FROM " + USER_DEVICE_TABLE + " WHERE site_user_id=? AND device_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, site_user_id);
		preStatement.setString(2, device_id);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			deviceId = rs.getString(1);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, ",deviceId=" + deviceId,
				sql + " = " + site_user_id + "," + device_id);
		return deviceId;
	}

	public UserDeviceBean queryDefaultDevice(String site_user_id) throws SQLException {
		UserDeviceBean deviceBean = new UserDeviceBean();
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,device_id,user_device_pubk,device_name,max(active_time) FROM "
				+ USER_DEVICE_TABLE + " WHERE site_user_id=? LIMIT 1;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, site_user_id);

		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			deviceBean.setSiteUserId(rs.getString(1));
			deviceBean.setDeviceId(rs.getString(2));
			deviceBean.setUserDevicePubk(rs.getString(3));
			deviceBean.setDeviceName(rs.getString(4));
			deviceBean.setActiveTime(rs.getLong(5));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, deviceBean.toString(), sql + " = " + site_user_id);
		return deviceBean;
	}

	public List<UserDeviceBean> queryDeviceList(String site_user_id) throws SQLException {
		List<UserDeviceBean> devicesBean = new ArrayList<UserDeviceBean>();
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,device_id,user_device_pubk,device_name,active_time FROM " + USER_DEVICE_TABLE
				+ " WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, site_user_id);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			UserDeviceBean deviceBean = new UserDeviceBean();
			deviceBean.setSiteUserId(rs.getString(1));
			deviceBean.setDeviceId(rs.getString(2));
			deviceBean.setUserDevicePubk(rs.getString(3));
			deviceBean.setDeviceName(rs.getString(4));
			deviceBean.setActiveTime(rs.getLong(5));
			devicesBean.add(deviceBean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, devicesBean.size() + "", sql + " = " + site_user_id);

		return devicesBean;
	}

	public List<UserDeviceBean> queryOnlineDeviceList(String siteUserId) throws SQLException {
		List<UserDeviceBean> devicesBean = new ArrayList<UserDeviceBean>();
		long startTime = System.currentTimeMillis();
		String sql = "SELECT a.site_user_id,a.device_id,a.login_time,b.user_device_pubk,b.device_name,b.active_time FROM "
				+ USER_SESSION_TABLE + " AS a LEFT JOIN " + USER_DEVICE_TABLE
				+ " AS b WHERE a.device_id=b.device_id and a.site_user_id=b.site_user_id and a.site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);

		ResultSet rs = preStatement.executeQuery();
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

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, devicesBean.size() + "", sql + " = " + siteUserId);

		return devicesBean;
	}

	public String queryUserToken(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String userToken = null;
		String sql = "SELECT user_token,max(active_time) FROM " + SQLConst.SITE_USER_DEVICE
				+ " WHERE site_user_id=? LIMIT 1;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);

		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			userToken = rs.getString(1);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userToken, sql + siteUserId);
		return userToken;
	}
}
