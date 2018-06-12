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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.GroupProto;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserGroupBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 操作用户表 && 用户-群组表 相关
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-14 17:54:51
 */
public class SiteUserGroupDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUserGroupDao.class);
	private final String USER_GROUP_TABLE = SQLConst.SITE_USER_GROUP;
	private static SiteUserGroupDao instance = new SiteUserGroupDao();

	public static SiteUserGroupDao getInstance() {
		return instance;
	}

	public int queryGroupMembersCount(String siteGroupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT count(site_user_id) FROM " + USER_GROUP_TABLE + " WHERE site_group_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
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

	/**
	 * <pre>
	 * 	添加群组新成员,分为两步
	 * 		1.select，防止已经为群成员
	 * 		2.insert，如果select不到用户
	 * </pre>
	 * 
	 * @param createUserId
	 * @param groupId
	 * @param memberIds
	 * @return
	 * @throws SQLException
	 */
	public boolean addGroupMember(String siteUserId, String groupId, int status) throws SQLException {
		GroupMemberBean bean = getGroupMember(siteUserId, groupId);
		if (bean == null || StringUtils.isBlank(bean.getUserId())) {
			return insertGroupMember(siteUserId, groupId, status);
		}
		return false;
	}

	public boolean insertGroupMember(String siteUserId, String groupId, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_GROUP_TABLE
				+ "(site_user_id,site_group_id,user_role,add_time) VALUES(?,?,?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setString(2, groupId);
			ps.setInt(3, GroupProto.GroupMemberRole.OWNER_VALUE);
			ps.setLong(4, System.currentTimeMillis());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, groupId,
				GroupProto.GroupMemberRole.OWNER_VALUE);
		return result > 0;
	}

	public List<SimpleGroupBean> queryUserGroupList(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleGroupBean> groupList = new ArrayList<SimpleGroupBean>();
		String sql = "SELECT a.site_group_id,b.group_name,b.group_photo FROM " + USER_GROUP_TABLE + " AS a LEFT JOIN "
				+ SQLConst.SITE_GROUP_PROFILE
				+ " AS b ON a.site_group_id=b.site_group_id WHERE a.site_user_id=? AND b.group_status>0;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			while (rs.next()) {
				SimpleGroupBean bean = new SimpleGroupBean();
				bean.setGroupId(rs.getString(1));
				bean.setGroupName(rs.getString(2));
				bean.setGroupPhoto(rs.getString(3));
				groupList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, groupList, sql, siteUserId);
		return groupList;
	}

	public List<SimpleGroupBean> queryUserGroupList(String siteUserId, int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleGroupBean> groupList = new ArrayList<SimpleGroupBean>();

		String sql = "SELECT a.site_group_id,b.group_name,b.group_photo FROM " + USER_GROUP_TABLE + " AS a LEFT JOIN "
				+ SQLConst.SITE_GROUP_PROFILE
				+ " AS b WHERE a.site_group_id=b.site_group_id AND b.group_status>0 AND a.site_user_id=? LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setInt(2, startNum);
			pst.setInt(3, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				SimpleGroupBean bean = new SimpleGroupBean();
				bean.setGroupId(rs.getString(1));
				bean.setGroupName(rs.getString(2));
				bean.setGroupPhoto(rs.getString(3));
				groupList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, groupList, sql, siteUserId);
		return groupList;
	}

	public int queryUserGroupCount(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "SELECT COUNT(*) FROM " + USER_GROUP_TABLE + " AS a LEFT JOIN " + SQLConst.SITE_GROUP_PROFILE
				+ " AS b WHERE a.site_group_id=b.site_group_id AND b.group_status>0 AND a.site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId);
		return result;
	}

	public List<String> queryUserGroupsId(String userId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> groupsIdList = new ArrayList<String>();
		String sql = "SELECT site_group_id FROM site_user_group WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, userId);

			rs = pst.executeQuery();
			while (rs.next()) {
				String groupId = rs.getString(1);
				groupsIdList.add(groupId);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, groupsIdList.toString(), sql, userId);
		return groupsIdList;
	}

	public List<String> queryGroupMembersId(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> userIdList = new ArrayList<String>();
		String sql = "SELECT DISTINCT site_user_id FROM " + USER_GROUP_TABLE + " WHERE site_group_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);

			rs = pst.executeQuery();
			while (rs.next()) {
				String userId = rs.getString(1);
				userIdList.add(userId);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userIdList.toString(), sql, groupId);
		return userIdList;
	}

	public List<GroupMemberBean> queryGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		String sql = "SELECT DISTINCT a.site_user_id,b.user_name,b.user_photo,a.user_role FROM " + USER_GROUP_TABLE
				+ " AS a LEFT JOIN " + SQLConst.SITE_USER_PROFILE
				+ " AS b ON a.site_user_id=b.site_user_id WHERE a.site_group_id=? limit ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);
			pst.setInt(2, startNum);
			pst.setInt(3, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				GroupMemberBean member = new GroupMemberBean();
				member.setUserId(rs.getString(1));
				member.setUserName(rs.getString(2));
				member.setUserPhoto(rs.getString(3));
				member.setUserRole(rs.getInt(4));
				membersList.add(member);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, membersList.size(), sql, groupId, startNum, pageSize);
		return membersList;
	}

	public int queryNonGroupMemberNum(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int num = 0;
		String sql = "SELECT COUNT(site_user_id) FROM " + SQLConst.SITE_USER_PROFILE
				+ " WHERE site_user_id NOT IN (SELECT DISTINCT site_user_id FROM " + SQLConst.SITE_USER_GROUP
				+ " WHERE site_group_id=?);";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, num, sql, groupId);
		return num;
	}

	public List<GroupMemberBean> queryNonGroupMemberList(String groupId, int pageNum, int pageSize)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		int startNum = (pageNum - 1) * pageSize;
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + SQLConst.SITE_USER_PROFILE
				+ " WHERE site_user_id NOT IN (SELECT DISTINCT site_user_id FROM " + SQLConst.SITE_USER_GROUP
				+ " WHERE site_group_id=?) LIMIT ?,?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, groupId);
			pst.setInt(2, startNum);
			pst.setInt(3, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				GroupMemberBean member = new GroupMemberBean();
				member.setUserId(rs.getString(1));
				member.setUserName(rs.getString(2));
				member.setUserPhoto(rs.getString(3));
				member.setUserStatus(rs.getInt(4));
				member.setUserRole(GroupProto.GroupMemberRole.NONMEMBER_VALUE);
				membersList.add(member);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, membersList.toString(), sql, groupId, startNum, pageSize);
		return membersList;
	}

	public int queryUserFriendNonGroupMemberNum(String siteUserId, String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "SELECT a.site_friend_id,b.user_name,b.user_photo FROM " + SQLConst.SITE_USER_FRIEND
				+ " AS a LEFT JOIN " + SQLConst.SITE_USER_PROFILE
				+ " AS b WHERE a.site_friend_id=b.site_user_id AND a.site_user_id=? AND a.site_friend_id NOT IN (SELECT DISTINCT site_user_id FROM "
				+ SQLConst.SITE_USER_GROUP + " WHERE site_group_id=?);";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, groupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, groupId);
		return result;
	}

	/**
	 * 查询用户好友中非群成员用户
	 * 
	 * @param groupId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public List<SimpleUserBean> queryUserFriendNonGroupMemberList(String siteUserId, String groupId, int pageNum,
			int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userList = new ArrayList<SimpleUserBean>();
		int startNum = (pageNum - 1) * pageSize;
		String sql = "SELECT a.site_friend_id,b.user_name,b.user_photo FROM " + SQLConst.SITE_USER_FRIEND
				+ " AS a LEFT JOIN " + SQLConst.SITE_USER_PROFILE
				+ " AS b WHERE a.site_friend_id=b.site_user_id AND a.site_user_id=? AND a.site_friend_id NOT IN (SELECT DISTINCT site_user_id FROM "
				+ SQLConst.SITE_USER_GROUP + " WHERE site_group_id=?) LIMIT ?,?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, groupId);
			pst.setInt(3, startNum);
			pst.setInt(4, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				SimpleUserBean userBean = new SimpleUserBean();
				userBean.setUserId(rs.getString(1));
				userBean.setUserName(rs.getString(2));
				userBean.setUserPhoto(rs.getString(3));
				userList.add(userBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userList, sql, siteUserId, groupId, startNum, pageSize);
		return userList;
	}

	public GroupMemberBean getGroupMember(String siteUserId, String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,user_role FROM " + USER_GROUP_TABLE
				+ " WHERE site_user_id=? AND site_group_id=?;";

		GroupMemberBean member = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, groupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				member = new GroupMemberBean();
				member.setUserId(rs.getString(1));
				member.setUserRole(rs.getInt(2));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, member, sql, siteUserId, groupId);
		return member;
	}

	public List<String> checkGroupMember(String siteGroupId, List<String> userIds) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id FROM " + USER_GROUP_TABLE
				+ " WHERE site_group_id=? AND site_user_id IN ({});";

		StringBuilder userIdBuider = new StringBuilder();
		for (int i = 0; i < userIds.size(); i++) {
			if (i == 0) {
				userIdBuider.append("'");
				userIdBuider.append(userIds.get(i));
				userIdBuider.append("'");
			} else {
				userIdBuider.append(",");
				userIdBuider.append("'");
				userIdBuider.append(userIds.get(i));
				userIdBuider.append("'");
			}
		}
		sql = StringHelper.format(sql, userIdBuider.toString());
		logger.debug("check group member sql: {}", sql);

		List<String> userList = new ArrayList<String>();

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteGroupId);

			rs = pst.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString(1));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userList, sql, siteGroupId);
		return userList;
	}

	public boolean deleteGroupMember(String groupId, List<String> userIds) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_GROUP_TABLE + " WHERE site_user_id=? AND site_group_id=?;";
		int result = 0;

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			for (String siteUserId : userIds) {
				try {
					ps = conn.prepareStatement(sql);
					ps.setString(1, siteUserId);
					ps.setString(2, groupId);
					result += ps.executeUpdate();

					ps.clearParameters();
					ps.close();
					LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, groupId);
				} catch (SQLException e) {
					logger.error("delete groupId={} memberUserId={} error", groupId, siteUserId);
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, userIds.size(), sql);
		return result >= userIds.size();
	}

	public UserGroupBean getUserGroupSetting(String siteUserId, String siteGroupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT mute FROM " + USER_GROUP_TABLE + " WHERE site_user_id=? AND site_group_id=?;";

		UserGroupBean bean = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, siteGroupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				bean = new UserGroupBean();
				bean.setMute(rs.getBoolean(1));
				bean.setSiteGroupId(siteGroupId);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, siteUserId, siteGroupId);
		return bean;
	}

	public boolean updateUserGroupSetting(String siteUserId, UserGroupBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_GROUP_TABLE + " SET  mute=? WHERE site_user_id=? AND site_group_id=?;";

		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setBoolean(1, bean.isMute());
			ps.setString(2, siteUserId);
			ps.setString(3, bean.getSiteGroupId());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, siteUserId, bean);
		return result > 0;
	}

	public boolean queryMute(String siteUserId, String siteGroupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		boolean result = true;
		String sql = "SELECT mute FROM " + USER_GROUP_TABLE + " WHERE site_user_id=? AND site_group_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, siteGroupId);

			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getBoolean(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, siteGroupId);
		return result;
	}

	public boolean updateMute(String siteUserId, String siteGroupId, boolean mute) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_GROUP_TABLE + " SET  mute=? WHERE site_user_id=? AND site_group_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setBoolean(1, mute);
			ps.setString(2, siteUserId);
			ps.setString(3, siteGroupId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, mute, siteUserId, siteGroupId);
		return result > 0;
	}
}
