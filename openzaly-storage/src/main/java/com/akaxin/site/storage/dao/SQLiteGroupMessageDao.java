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
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;

/**
 * 群消息数据库相关操作
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 16:15:32
 */
public class SQLiteGroupMessageDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteGroupMessageDao.class);
	private final String GROUP_MESSAGE_TABLE = SQLConst.SITE_GROUP_MESSAGE;
	private final String GROUP_POINTER_TABLE = SQLConst.SITE_GROUP_MESSAGE_POINTER;
	private static SQLiteGroupMessageDao instance = new SQLiteGroupMessageDao();

	public static SQLiteGroupMessageDao getInstance() {
		return instance;
	}

	public boolean saveGroupMessage(GroupMessageBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + GROUP_MESSAGE_TABLE
				+ "(site_group_id,msg_id,send_user_id,send_device_id,msg_type,content,msg_time) VALUES(?,?,?,?,?,?,?);";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getSiteGroupId());
		preStatement.setString(2, bean.getMsgId());
		preStatement.setString(3, bean.getSendUserId());
		preStatement.setString(4, bean.getSendDeviceId());
		preStatement.setLong(5, bean.getMsgType());
		preStatement.setString(6, bean.getContent());
		preStatement.setLong(7, bean.getMsgTime());

		int insertResult = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, insertResult, sql, bean.getSiteGroupId(), bean.getMsgId(),
				bean.getSendUserId(), bean.getSendDeviceId(), bean.getMsgType(), bean.getContent(), bean.getMsgTime());

		return insertResult == 1;
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
				+ GROUP_MESSAGE_TABLE
				+ " AS a LEFT JOIN site_group_profile AS b WHERE a.site_group_id=b.site_group_id AND a.site_group_id=? AND a.id>? AND b.group_status=1 AND a.send_device_id IS NOT ? LIMIT ?;";

		start = queryGroupPointer(groupId, userId, deviceId, start);

		if (start == 0) {
			start = queryMaxGroupPointerWithUser(groupId, userId);
		}

		PreparedStatement statement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		statement.setString(1, groupId);
		statement.setLong(2, start);
		statement.setString(3, deviceId);
		statement.setInt(4, limit);

		ResultSet rs = statement.executeQuery();

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
		PreparedStatement statement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		// statement.setString(1, msgIdBuider.toString());

		ResultSet rs = statement.executeQuery();
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
		PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		pStatement.setString(1, groupId);
		pStatement.setString(2, userId);
		pStatement.setString(3, deviceId);
		pStatement.setLong(4, finish);
		result = pStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, groupId, userId, deviceId, finish);
		return result == 1;
	}

	private int updateGroupPointer(String groupId, String userId, String deviceId, long finish) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + GROUP_POINTER_TABLE
				+ " SET pointer=? WHERE site_user_id=? AND site_group_id=? AND device_id=?;";

		PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		pStatement.setLong(1, finish);
		pStatement.setString(2, userId);
		pStatement.setString(3, groupId);
		pStatement.setString(4, deviceId);
		result = pStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, finish, userId, groupId, deviceId);
		return result;
	}

	public long queryGroupPointer(String groupId, String userId, String deviceId, long start) {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT pointer FROM " + GROUP_POINTER_TABLE
				+ " WHERE site_user_id=? AND site_group_id=? AND device_id=?;";
		try {
			PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
			pStatement.setString(1, userId);
			pStatement.setString(2, groupId);
			pStatement.setString(3, deviceId);

			ResultSet prs = pStatement.executeQuery();
			if (prs.next()) {
				pointer = prs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("query group message pointer error.", e);
		}

		LogUtils.dbDebugLog(logger, startTime, "pointer:" + pointer + ",start:" + start, sql, userId, groupId,
				deviceId);
		return pointer > start ? pointer : start;
	}

	private long queryMaxGroupPointerWithUser(String groupId, String siteUserId) {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT max(pointer) FROM " + GROUP_POINTER_TABLE + " WHERE site_user_id=? AND site_group_id=?;";
		try {
			PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
			pStatement.setString(1, siteUserId);
			pStatement.setString(2, groupId);

			ResultSet prs = pStatement.executeQuery();
			if (prs.next()) {
				pointer = prs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("query group message pointer error.", e);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId, groupId);
		return pointer;
	}

	public long queryMaxGroupPointer(String groupId) {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT MAX(id) FROM " + GROUP_MESSAGE_TABLE + " WHERE site_group_id=?;";
		try {
			PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
			pStatement.setString(1, groupId);

			ResultSet prs = pStatement.executeQuery();
			if (prs.next()) {
				pointer = prs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("query max group message pointer error.", e);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, groupId);
		return pointer;
	}

	public long queryMaxUserGroupPointer(String groupId, String siteUserId) {
		long startTime = System.currentTimeMillis();
		long pointer = 0;
		String sql = "SELECT MAX(pointer) FROM " + GROUP_POINTER_TABLE + " WHERE site_user_id=? AND site_group_id=?;";

		try {
			PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
			pStatement.setString(1, groupId);

			ResultSet prs = pStatement.executeQuery();
			if (prs.next()) {
				pointer = prs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error("query max user group message pointer error.", e);
		}

		LogUtils.dbDebugLog(logger, startTime, pointer, sql, groupId);
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
		PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preparedStatement.setLong(1, startTimeOfDay);
		preparedStatement.setLong(2, endTimeOfDay);
		ResultSet resultSet = preparedStatement.executeQuery();
		int groupCount = resultSet.getInt(1);
		LogUtils.dbDebugLog((logger), startTime, groupCount, sql);
		return groupCount;
	}

	public boolean delUserMessage(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + GROUP_MESSAGE_TABLE + " WHERE  send_user_id = ? ";
		String sqlP = "DELETE FROM " + GROUP_POINTER_TABLE + " WHERE  site_user_id = ? ";

		PreparedStatement statement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		statement.setString(1, siteUserId);
		int res1 = statement.executeUpdate();
		PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sqlP);
		preparedStatement.setString(1, siteUserId);
		int res2 = preparedStatement.executeUpdate();
		if (res1 > 0 && res2 > 0) {
			LogUtils.dbDebugLog(logger, startTime, res1 + "," + res2, sql, "true");

			return true;
		}
		LogUtils.dbDebugLog(logger, startTime, res1 + "," + res2, sql, "false");

		return false;
	}

	public List<String> queryMessageFile(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "select content from (select * from " + GROUP_MESSAGE_TABLE
				+ " where msg_type in (9,13)) t where send_user_id = ?";
		PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preparedStatement.setString(1, siteUserId);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<String> groupFiles = new ArrayList<>();
		while (rs.next()) {
			groupFiles.add(rs.getString(1));
		}
		LogUtils.dbDebugLog(logger, startTime, rs, sql);
		return groupFiles;
	}
}
