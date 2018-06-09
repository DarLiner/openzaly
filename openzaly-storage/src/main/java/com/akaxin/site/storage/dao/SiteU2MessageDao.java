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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.common.utils.TimeFormats;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:23
 */
public class SiteU2MessageDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteU2MessageDao.class);
	private static final String USER2_MESSAGE_TABLE = SQLConst.SITE_USER_MESSAGE;
	private static final String USER2_MESSAGE_POINATER_TABLE = SQLConst.SITE_MESSAGE_POINTER;
	private static SiteU2MessageDao instance = new SiteU2MessageDao();

	public static SiteU2MessageDao getInstance() {
		return instance;
	}

	public boolean saveU2Message(U2MessageBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER2_MESSAGE_TABLE
				+ "(site_user_id,msg_id,send_user_id,msg_type,content,device_id,ts_key,msg_time) VALUES(?,?,?,?,?,?,?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DatabaseConnection.getConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, bean.getSiteUserId());
			pst.setString(2, bean.getMsgId());
			pst.setString(3, bean.getSendUserId());
			pst.setLong(4, bean.getMsgType());
			pst.setString(5, bean.getContent());
			pst.setString(6, bean.getDeviceId());
			pst.setString(7, bean.getTsKey());
			pst.setLong(8, bean.getMsgTime());

			result = pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getMsgId(), bean.getSendUserId(),
				bean.getMsgType(), bean.getContent(), bean.getDeviceId(), bean.getTsKey(), bean.getMsgTime());
		return result == 1;
	}

	public List<U2MessageBean> getU2Message(String userId, String deviceId, long start, long limit)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<U2MessageBean> msgList = new ArrayList<U2MessageBean>();
		String sql = "SELECT id,site_user_id,msg_id,send_user_id,msg_type,content,device_id,ts_key,msg_time FROM "
				+ USER2_MESSAGE_TABLE + " WHERE site_user_id=? AND id>? LIMIT ?;";

		long pointer = queryU2MessagePointer(userId, deviceId);
		start = start > pointer ? start : pointer;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, userId);
			pst.setLong(2, start);
			pst.setLong(3, limit);

			rs = pst.executeQuery();
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

				msgList.add(u2MsgBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, msgList.size(), sql, userId, start, limit);
		return msgList;
	}

	public List<U2MessageBean> queryU2MessageByMsgId(List<String> msgIds) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT id,site_user_id,msg_id,send_user_id,msg_type,msg_time FROM " + USER2_MESSAGE_TABLE
				+ " WHERE msg_id in ({});";

		List<U2MessageBean> msgList = new ArrayList<U2MessageBean>();

		StringBuilder msgIdBuider = new StringBuilder();
		for (int i = 0; i < msgIds.size(); i++) {
			if (i == 0) {
				msgIdBuider.append("'");
				msgIdBuider.append(msgIds.get(i));
				msgIdBuider.append("'");
			} else {
				msgIdBuider.append(",");
				msgIdBuider.append("'");
				msgIdBuider.append(msgIds.get(i));
				msgIdBuider.append("'");
			}
		}

		sql = StringHelper.format(sql, msgIdBuider.toString());

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {
				U2MessageBean bean = new U2MessageBean();
				bean.setId(rs.getInt(1));
				bean.setSiteUserId(rs.getString(2));
				bean.setMsgId(rs.getString(3));
				bean.setSendUserId(rs.getString(4));
				bean.setMsgType(rs.getInt(5));
				bean.setMsgTime(rs.getLong(6));
				msgList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, msgList, sql, msgIdBuider.toString());
		return msgList;
	}

	public long queryU2MessagePointer(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT pointer FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id=? AND device_id=?;";

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
				pointer = rs.getLong(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId, deviceId);
		return pointer == 0 ? queryMaxU2MessagePointer(siteUserId) - 10 : pointer;
	}

	public long queryMaxU2MessagePointer(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT max(pointer) FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId);
		return pointer;
	}

	/**
	 * 查找最大的消息id，消息id在游标表中为游标
	 *
	 * @param siteUserId
	 * @return
	 * @throws SQLException
	 */
	public long queryMaxU2MessageId(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long maxPointer = 0;
		String sql = "SELECT max(id) FROM " + USER2_MESSAGE_TABLE + " WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				maxPointer = rs.getLong(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, maxPointer, sql, siteUserId);
		return maxPointer;
	}

	public boolean updateU2MessagePointer(String userId, String deviceId, long finish) throws SQLException {
		if (checkMsgPointer(userId, deviceId)) {
			return updateU2Pointer(userId, deviceId, finish);
		} else {
			return addU2Pointer(userId, deviceId, finish);
		}

	}

	public boolean addU2Pointer(String siteUserId, String deviceId, long finish) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER2_MESSAGE_POINATER_TABLE + "(site_user_id,pointer,device_id) VALUES(?,?,?)";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setLong(2, finish);
			ps.setString(3, deviceId);
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, finish, deviceId);
		return result == 1;
	}

	public boolean updateU2Pointer(String siteUserId, String deviceId, long finish) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER2_MESSAGE_POINATER_TABLE + " SET pointer=? WHERE site_user_id=? AND device_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setLong(1, finish);
			ps.setString(2, siteUserId);
			ps.setString(3, deviceId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, finish, siteUserId, deviceId);
		return result == 1;
	}

	public boolean checkMsgPointer(String siteUserId, String deviceId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String querySql = "select pointer from " + USER2_MESSAGE_POINATER_TABLE
				+ " WHERE site_user_id=? AND device_id=?";
		Long pointer = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(querySql);
			pst.setString(1, siteUserId);
			pst.setString(2, deviceId);

			rs = pst.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("check msg pointer error. siteUserId={} deviceI={}", siteUserId, deviceId);
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, siteUserId, deviceId);
		return pointer != null;
	}

	public int queryNumMessagePerDay(long now, int day) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT COUNT(*) FROM " + USER2_MESSAGE_TABLE + " WHERE msg_time BETWEEN ? and ? ";
		long startTimeOfDay = TimeFormats.getStartTimeOfDay(now);
		long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
		if (day != 0) {
			startTimeOfDay = startTimeOfDay - TimeUnit.DAYS.toMillis(day);
			endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
		}

		int u2Count = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, startTimeOfDay);
			pst.setLong(2, endTimeOfDay);

			rs = pst.executeQuery();
			if (rs.next()) {
				u2Count = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, u2Count, sql);
		return u2Count;
	}

	public boolean delUserMessage(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql1 = "DELETE FROM " + USER2_MESSAGE_TABLE + " WHERE site_user_id =? or send_user_id =?";
		String sql2 = "DELETE FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id =? ";

		int res1 = 0;
		int res2 = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql1);
			ps.setString(1, siteUserId);
			ps.setString(2, siteUserId);
			res1 = ps.executeUpdate();

			ps.clearParameters();
			ps.close();

			ps = conn.prepareStatement(sql2);
			ps.setString(1, siteUserId);
			res2 = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, res1, sql1, siteUserId);
		LogUtils.dbDebugLog(logger, startTime, res2, sql2, siteUserId);
		return false;
	}

	public List<String> queryMessageFile(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "select content from (select * from " + USER2_MESSAGE_TABLE
				+ " where msg_type in (7,8,11,12)) t where site_user_id=? or send_user_id=?;";

		ArrayList<String> u2Files = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, siteUserId);

			rs = pst.executeQuery();
			while (rs.next()) {
				u2Files.add(rs.getString(1));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, rs, sql, siteUserId, siteUserId);
		return u2Files;
	}

}
