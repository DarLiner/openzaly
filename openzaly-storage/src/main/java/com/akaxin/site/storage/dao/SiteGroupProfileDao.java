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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.TimeFormats;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.util.SqlUtils;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-08 15:53:25
 */
public class SiteGroupProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteGroupProfileDao.class);
	private static final String GROUP_PROFILE_TABLE = SQLConst.SITE_GROUP_PROFILE;

	private static SiteGroupProfileDao instance = new SiteGroupProfileDao();

	public static SiteGroupProfileDao getInstance() {
		return instance;
	}

	public List<SimpleGroupBean> queryGroupList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_group_id,group_name,group_photo FROM " + GROUP_PROFILE_TABLE
				+ " WHERE group_status>0 LIMIT ?,?;";
		List<SimpleGroupBean> beanList = new ArrayList<SimpleGroupBean>();

		int startNum = (pageNum - 1) * pageSize;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();

			pst = SQLiteJDBCManager.getConnection().prepareStatement(sql);
			pst.setInt(1, startNum);
			pst.setInt(2, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				SimpleGroupBean bean = new SimpleGroupBean();
				bean.setGroupId(rs.getString(1));
				bean.setGroupName(rs.getString(2));
				bean.setGroupPhoto(rs.getString(3));
				beanList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, beanList.size(), sql, startNum, pageSize);
		return beanList;
	}

	public GroupProfileBean saveGroupProfile(GroupProfileBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + GROUP_PROFILE_TABLE
				+ "(site_group_id,group_name,group_photo,group_notice,group_status,create_user_id,close_invite_group_chat,create_time) VALUES(?,?,?,?,1,?,?,?);";

		if (bean.getGroupId() == null) {
			bean.setGroupId(UUID.randomUUID().toString());
		}

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getGroupId());
			ps.setString(2, bean.getGroupName());
			ps.setString(3, bean.getGroupPhoto());
			ps.setString(4, bean.getGroupNotice());
			ps.setString(5, bean.getCreateUserId());
			ps.setBoolean(6, true);// 默认允许群成员添加新的群聊成员?
			ps.setLong(7, bean.getCreateTime());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getGroupId());
		return result > 0 ? bean : null;
	}

	public GroupProfileBean queryGroupProfile(String siteGroupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_group_id,group_name,group_photo,group_notice,ts_status,create_user_id,group_status,close_invite_group_chat,create_time FROM "
				+ GROUP_PROFILE_TABLE + " WHERE site_group_id=?;";
		GroupProfileBean profileBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteGroupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				profileBean = new GroupProfileBean();
				profileBean.setGroupId(rs.getString(1));
				profileBean.setGroupName(rs.getString(2));
				profileBean.setGroupPhoto(rs.getString(3));
				profileBean.setGroupNotice(rs.getString(4));
				profileBean.setTsStatus(rs.getInt(5));
				profileBean.setCreateUserId(rs.getString(6));
				profileBean.setGroupStatus(rs.getInt(7));
				profileBean.setCloseInviteGroupChat(rs.getBoolean(8));
				profileBean.setCreateTime(rs.getLong(9));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, profileBean, sql, siteGroupId);
		return profileBean;
	}

	public GroupProfileBean querySimpleGroupProfile(String siteGroupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		GroupProfileBean profileBean = null;
		String sql = "SELECT site_group_id,group_name,group_photo FROM " + GROUP_PROFILE_TABLE
				+ " WHERE site_group_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteGroupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				profileBean = new GroupProfileBean();
				profileBean.setGroupId(rs.getString(1));
				profileBean.setGroupName(rs.getString(2));
				profileBean.setGroupPhoto(rs.getString(3));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, profileBean, sql, siteGroupId);
		return profileBean;
	}

	/**
	 * <pre>
	 * status = 0:删除的群组
	 * status = 1:正常的群
	 * </pre>
	 *
	 * @param siteGroupId
	 * @return
	 * @throws SQLException
	 */
	public int queryGroupStatus(String siteGroupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT group_status FROM " + GROUP_PROFILE_TABLE + " WHERE site_group_id=?;";
		int result = 0;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, siteGroupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteGroupId);
		return result;
	}

	public int updateGroupProfile(GroupProfileBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + GROUP_PROFILE_TABLE + " {} WHERE site_group_id=?;";
		int result = 0;

		Map<String, String> sqlMap = new HashMap<String, String>();
		sqlMap.put("group_name", bean.getGroupName());
		sqlMap.put("group_photo", bean.getGroupPhoto());
		sqlMap.put("group_notice", bean.getGroupNotice());

		SqlUtils.SqlBean sqlBean = SqlUtils.buildUpdateSql(sql, sqlMap);
		String realSql = sqlBean.getSql();

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(realSql);
			for (Integer index : sqlBean.getParams().keySet()) {
				ps.setString(index, sqlBean.getParams().get(index));
			}
			ps.setString(sqlBean.getParams().size() + 1, bean.getGroupId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, realSql, bean.getGroupName(), bean.getGroupPhoto(),
				bean.getGroupNotice(), bean.getGroupId());
		return result;
	}

	/**
	 * 更新是否可以邀请群聊的状态值
	 *
	 * @param bean
	 * @return
	 * @throws SQLException
	 */
	public int updateGroupIGC(GroupProfileBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + GROUP_PROFILE_TABLE + " SET close_invite_group_chat=? WHERE site_group_id=?;";
		int result = 0;

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setBoolean(1, bean.isCloseInviteGroupChat());
			ps.setString(2, bean.getGroupId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.isCloseInviteGroupChat(), bean.getGroupId());
		return result;
	}

	public int updateGroupOwer(String siteUserId, String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + GROUP_PROFILE_TABLE + " SET create_user_id=? WHERE site_group_id=?;";
		int result = 0;

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, groupId);
			result = pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, groupId);
		return result;
	}

	public boolean deleteGroupProfile(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + GROUP_PROFILE_TABLE + " SET group_status=0 WHERE site_group_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql);
		return result > 0;
	}

	public String getGrouMaster(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String siteUserId = null;
		String sql = "SELECT create_user_id FROM " + GROUP_PROFILE_TABLE + " WHERE site_group_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				siteUserId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, siteUserId, sql, groupId);
		return siteUserId;
	}

	public int getTotalGroupNum() throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT COUNT(*) FROM " + GROUP_PROFILE_TABLE + " WHERE group_status = 1;";

		int groupNum = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			if (rs.next()) {
				groupNum = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, groupNum, sql);
		return groupNum;
	}

	public int getGroupNum(long now, int day) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT COUNT(*) FROM " + GROUP_PROFILE_TABLE + " WHERE create_time < ? and group_status = 1 ";

		long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
		if (day != 0) {
			endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
		}

		int groupNum = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, endTimeOfDay);

			rs = pst.executeQuery();
			if (rs.next()) {
				groupNum = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, groupNum, sql);
		return groupNum;

	}

	public boolean rmGroupProfile(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql1 = "DELETE FROM " + GROUP_PROFILE_TABLE + "  WHERE site_group_id=?;";
		String sql2 = "DELETE FROM " + SQLConst.SITE_USER_GROUP + "  WHERE site_group_id=?;";

		int result1;
		int result2;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DatabaseConnection.getConnection();

			pst = conn.prepareStatement(sql1);
			pst.setString(1, groupId);
			result1 = pst.executeUpdate();

			pst.clearParameters();
			pst.close();

			pst = conn.prepareStatement(sql2);
			pst.setString(1, groupId);
			result2 = pst.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst);
		}

		LogUtils.dbDebugLog(logger, startTime, result1, sql1, groupId);
		LogUtils.dbDebugLog(logger, startTime, result2, sql2, groupId);
		return result1 > 0 && result2 > 0;

	}
}