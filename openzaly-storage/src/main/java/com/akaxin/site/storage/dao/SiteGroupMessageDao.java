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
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 群消息数据库相关操作
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 16:15:32
 */
public class SiteGroupMessageDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteGroupMessageDao.class);
	private final String GROUP_MESSAGE_TABLE = SQLConst.SITE_GROUP_MESSAGE;
	private final String GROUP_POINTER_TABLE = SQLConst.SITE_GROUP_MESSAGE_POINTER;
	private static SiteGroupMessageDao instance = new SiteGroupMessageDao();

	public static SiteGroupMessageDao getInstance() {
		return instance;
	}

	public boolean saveGroupMessage(GroupMessageBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + GROUP_MESSAGE_TABLE
				+ "(site_group_id,msg_id,send_user_id,send_device_id,msg_type,content,msg_time) VALUES(?,?,?,?,?,?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DatabaseConnection.getConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, bean.getSiteGroupId());
			pst.setString(2, bean.getMsgId());
			pst.setString(3, bean.getSendUserId());
			pst.setString(4, bean.getSendDeviceId());
			pst.setLong(5, bean.getMsgType());
			pst.setString(6, bean.getContent());
			pst.setLong(7, bean.getMsgTime());

			result = pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteGroupId(), bean.getMsgId(),
				bean.getSendUserId(), bean.getSendDeviceId(), bean.getMsgType(), bean.getContent(), bean.getMsgTime());

		return result == 1;
	}

	/**
	 * 查询的结果，排除发送者的deviceId
	 *
	 * @param groupId
	 * @param userId
	 * @param deviceId
	 * @param start
	 * @return
	 * @throws SQLException
	 */
	public List<GroupMessageBean> queryGroupMessage(String groupId, String userId, String deviceId, long start,
			int limit) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<GroupMessageBean> gmsgList = new ArrayList<GroupMessageBean>();
		String sql = "SELECT a.id,a.site_group_id,a.msg_id,a.send_user_id,a.send_device_id,a.msg_type,a.content,a.msg_time FROM "
				+ GROUP_MESSAGE_TABLE + " AS a LEFT JOIN " + SQLConst.SITE_GROUP_PROFILE
				+ " AS b ON a.site_group_id=b.site_group_id WHERE a.site_group_id=? AND a.id>? AND b.group_status=1 LIMIT ?;";

		start = queryGroupPointer(groupId, userId, deviceId, start);

		if (start == 0) {
			start = queryMaxGroupPointerWithUser(groupId, userId);
		}

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);
			pst.setLong(2, start);
			pst.setInt(3, limit);

			rs = pst.executeQuery();
			while (rs.next()) {
				GroupMessageBean gmsgBean = new GroupMessageBean();
				gmsgBean.setId(rs.getInt(1));
				gmsgBean.setSiteGroupId(rs.getString(2));
				gmsgBean.setMsgId(rs.getString(3));
				gmsgBean.setSendUserId(rs.getString(4));
				gmsgBean.setSendDeviceId(rs.getString(5));
				gmsgBean.setMsgType(rs.getInt(6));
				gmsgBean.setContent(rs.getString(7));
				gmsgBean.setMsgTime(rs.getLong(8));
				gmsgList.add(gmsgBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, gmsgList.size(), sql, groupId, start, deviceId, limit);
		return gmsgList;
	}

	public List<GroupMessageBean> queryGroupMessageByMsgId(List<String> msgIds) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT id,site_group_id,msg_id,send_user_id,msg_type,msg_time FROM " + GROUP_MESSAGE_TABLE
				+ " WHERE msg_id in ({});";
		List<GroupMessageBean> msgList = new ArrayList<GroupMessageBean>();
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
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			// statement.setString(1, msgIdBuider.toString());

			rs = pst.executeQuery();
			while (rs.next()) {
				GroupMessageBean bean = new GroupMessageBean();
				bean.setId(rs.getInt(1));
				bean.setSiteGroupId(rs.getString(2));
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

	public boolean updateGroupMessagePointer(String groupId, String siteUserId, String deviceId, long finishPointer)
			throws SQLException {
		int result = updateGroupPointer(groupId, siteUserId, deviceId, finishPointer);

		if (result >= 1) {
			return true;
		}
		return saveGroupPointer(groupId, siteUserId, deviceId, finishPointer);
	}

	public boolean saveGroupPointer(String groupId, String userId, String deviceId, long finish) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "INSERT INTO " + GROUP_POINTER_TABLE
				+ "(site_group_id,site_user_id,device_id,pointer) VALUES(?,?,?,?);";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);
			pst.setString(2, userId);
			pst.setString(3, deviceId);
			pst.setLong(4, finish);
			result = pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, groupId, userId, deviceId, finish);
		return result == 1;
	}

	private int updateGroupPointer(String groupId, String userId, String deviceId, long finish) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + GROUP_POINTER_TABLE
				+ " SET pointer=? WHERE site_group_id=? AND site_user_id=? AND device_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, finish);
			pst.setString(1, groupId);
			pst.setString(2, userId);
			pst.setString(3, deviceId);
			result = pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, finish, userId, groupId, deviceId);
		return result;
	}

	public long queryGroupPointer(String groupId, String siteUserId, String deviceId, long start) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT pointer FROM " + GROUP_POINTER_TABLE
				+ " WHERE site_group_id=? AND site_user_id=?  AND device_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);
			pst.setString(2, siteUserId);
			pst.setString(3, deviceId);

			rs = pst.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("query group message pointer error.", e);
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, "pointer:" + pointer + ",start:" + start, sql, siteUserId, groupId,
				deviceId);
		return pointer > start ? pointer : start;
	}

	private long queryMaxGroupPointerWithUser(String groupId, String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT max(pointer) FROM " + GROUP_POINTER_TABLE + " WHERE site_group_id=? AND site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);
			pst.setString(2, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId, groupId);
		return pointer;
	}

	public long queryMaxGroupPointer(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT MAX(id) FROM " + GROUP_MESSAGE_TABLE + " WHERE site_group_id=?;";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);

			rs = ps.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, groupId);
		return pointer;
	}

	public long queryMaxUserGroupPointer(String groupId, String siteUserId) {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT MAX(pointer) FROM " + GROUP_POINTER_TABLE + " WHERE site_group_id=? AND site_user_id=?;";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			ps.setString(2, siteUserId);

			rs = ps.executeQuery();
			if (rs.next()) {
				pointer = rs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("query max user group message pointer error.", e);
		} finally {
			DatabaseConnection.returnConnection(conn, ps, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId, groupId);
		return pointer;
	}

	public int queryNumMessagePerDay(long now, int day) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT COUNT(*) FROM " + GROUP_MESSAGE_TABLE + " WHERE msg_time BETWEEN ? and ? ";
		long startTimeOfDay = TimeFormats.getStartTimeOfDay(now);
		long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
		if (day != 0) {
			startTimeOfDay = startTimeOfDay - TimeUnit.DAYS.toMillis(day);
			endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
		}

		int groupCount = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			ps = conn.prepareStatement(sql);
			ps.setLong(1, startTimeOfDay);
			ps.setLong(2, endTimeOfDay);
			rs = ps.executeQuery();

			if (rs.next()) {
				groupCount = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps, rs);
		}

		LogUtils.dbDebugLog((logger), startTime, groupCount, sql, startTimeOfDay, endTimeOfDay);
		return groupCount;
	}

	public boolean delUserMessage(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		boolean result = false;
		String sql_msg = "DELETE FROM " + GROUP_MESSAGE_TABLE + " WHERE send_user_id = ? ";
		String sql_pointer = "DELETE FROM " + GROUP_POINTER_TABLE + " WHERE  site_user_id = ? ";

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = DatabaseConnection.getConnection();
			preparedStatement = conn.prepareStatement(sql_msg);
			preparedStatement.setString(1, siteUserId);
			int res1 = preparedStatement.executeUpdate();

			preparedStatement.clearParameters();
			preparedStatement.close();
			preparedStatement = conn.prepareStatement(sql_pointer);
			preparedStatement.setString(1, siteUserId);
			int res2 = preparedStatement.executeUpdate();
			if (res1 > 0 && res2 > 0) {
				result = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, preparedStatement);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql_msg, siteUserId);
		LogUtils.dbDebugLog(logger, startTime, result, sql_pointer, siteUserId);
		return false;
	}

	public List<String> queryMessageFile(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "select content from (select * from " + GROUP_MESSAGE_TABLE
				+ " where msg_type in (9,13)) t where send_user_id = ?";
		List<String> groupFiles = new ArrayList<String>();

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, siteUserId);
			rs = preparedStatement.executeQuery();

			while (rs.next()) {
				groupFiles.add(rs.getString(1));
			}
			LogUtils.dbDebugLog(logger, startTime, rs, sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, preparedStatement, rs);
		}
		return groupFiles;
	}
}
