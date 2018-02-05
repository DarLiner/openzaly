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
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:23
 */
public class SQLiteU2MessageDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteU2MessageDao.class);
	private static final String USER2_MESSAGE_TABLE = SQLConst.SITE_USER_MESSAGE;
	private static final String USER2_MESSAGE_POINATER_TABLE = SQLConst.SITE_MESSAGE_POINTER;
	private static SQLiteU2MessageDao instance = new SQLiteU2MessageDao();

	public static SQLiteU2MessageDao getInstance() {
		return instance;
	}

	public boolean saveU2Message(U2MessageBean u2Bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		boolean result = false;
		String insertSql = "INSERT INTO " + USER2_MESSAGE_TABLE
				+ "(site_user_id,msg_id,send_user_id,msg_type,content,device_id,ts_key,msg_time) VALUES(?,?,?,?,?,?,?,?);";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(insertSql);
		preStatement.setString(1, u2Bean.getSiteUserId());
		preStatement.setString(2, u2Bean.getMsgId());
		preStatement.setString(3, u2Bean.getSendUserId());
		preStatement.setLong(4, u2Bean.getMsgType());
		preStatement.setString(5, u2Bean.getContent());
		preStatement.setString(6, u2Bean.getDeviceId());
		preStatement.setString(7, u2Bean.getTsKey());
		preStatement.setLong(8, u2Bean.getMsgTime());
		int insertResult = preStatement.executeUpdate();

		if (insertResult == 1) {
			result = true;
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, insertSql + u2Bean.toString());

		return true;
	}

	public List<U2MessageBean> getU2Message(String userId, String deviceId, long start, long limit)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<U2MessageBean> u2MsgList = new ArrayList<U2MessageBean>();

		String sql = "SELECT id,site_user_id,msg_id,send_user_id,msg_type,content,device_id,ts_key,msg_time FROM "
				+ USER2_MESSAGE_TABLE + " WHERE site_user_id=? AND id>? LIMIT ?;";
		long pointer = queryU2MessagePointer(userId, deviceId);
		start = start > pointer ? start : pointer;

		PreparedStatement statement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		statement.setString(1, userId);
		statement.setLong(2, start);
		statement.setLong(3, limit);

		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			U2MessageBean u2MsgBean = new U2MessageBean();
			u2MsgBean.setId(rs.getInt(1));
			u2MsgBean.setSiteUserId(rs.getString(2));
			u2MsgBean.setMsgId(rs.getString(3));
			u2MsgBean.setSendUserId(rs.getString(4));
			u2MsgBean.setMsgType(rs.getInt(5));
			u2MsgBean.setContent(rs.getString(6));
			u2MsgBean.setDeviceId(rs.getString(7));
			u2MsgBean.setTsKey(rs.getString(8));
			u2MsgBean.setMsgTime(rs.getLong(9));

			u2MsgList.add(u2MsgBean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, GsonUtils.toJson(u2MsgList),
				sql + ",start=" + start + ",userId=" + userId + ",device_id=" + deviceId);

		return u2MsgList;
	}

	public long queryU2MessagePointer(String userId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT pointer FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id=? AND device_id=?;";
		PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		pStatement.setString(1, userId);
		pStatement.setString(2, deviceId);

		ResultSet prs = pStatement.executeQuery();
		if (prs.next()) {
			pointer = prs.getLong(1);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, "po=" + pointer,
				sql + ",userId=" + userId + ",device_id=" + deviceId);
		return pointer == 0 ? queryMaxU2MessagePointer(userId) : pointer;
	}

	public long queryMaxU2MessagePointer(String userId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT max(pointer) FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id=?;";
		PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		pStatement.setString(1, userId);

		ResultSet prs = pStatement.executeQuery();
		if (prs.next()) {
			pointer = prs.getLong(1);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, "po=" + pointer, sql + ",userId=" + userId);
		return pointer;
	}

	public boolean updateU2MessagePointer(String userId, String deviceId, long finish) throws SQLException {
		if (checkMsgPointer(userId, deviceId)) {
			return updateU2Pointer(userId, deviceId, finish);
		} else {
			return addU2Pointer(userId, deviceId, finish);
		}

	}

	public boolean addU2Pointer(String userId, String deviceId, long finish) throws SQLException {
		String insertSql = "INSERT INTO " + USER2_MESSAGE_POINATER_TABLE
				+ "(site_user_id,pointer,device_id) VALUES(?,?,?)";
		int result = 0;
		long startTime = System.currentTimeMillis();
		PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(insertSql);
		pStatement.setString(1, userId);
		pStatement.setLong(2, finish);
		pStatement.setString(3, deviceId);

		result = pStatement.executeUpdate();
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, insertSql + finish + "," + userId + "," + deviceId);

		return result == 1;
	}

	public boolean updateU2Pointer(String userId, String deviceId, long finish) throws SQLException {
		String updateSql = "UPDATE " + USER2_MESSAGE_POINATER_TABLE
				+ " SET pointer=? WHERE site_user_id=? AND device_id=?;";
		int result = 0;
		long startTime = System.currentTimeMillis();
		PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(updateSql);
		pStatement.setLong(1, finish);
		pStatement.setString(2, userId);
		pStatement.setString(3, deviceId);

		result = pStatement.executeUpdate();
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "",
				updateSql + "," + finish + "," + userId + "," + deviceId);

		return result == 1;
	}

	public boolean checkMsgPointer(String siteUserId, String deviceId) {
		String querySql = "select pointer from " + USER2_MESSAGE_POINATER_TABLE
				+ " WHERE site_user_id=? AND device_id=?";
		Long pointer = null;
		long startTime = System.currentTimeMillis();
		try {
			PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(querySql);
			pStatement.setString(1, siteUserId);
			pStatement.setString(2, deviceId);

			ResultSet rs = pStatement.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, "pointer=" + pointer, siteUserId + "," + deviceId);

		return pointer != null;
	}
}
