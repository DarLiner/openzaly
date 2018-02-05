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
import com.akaxin.proto.core.GroupProto;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 操作用户表 && 用户-群组表 相关
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-14 17:54:51
 */
public class SQLiteUserGroupDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUserGroupDao.class);
	private final String USER_GROUP_TABLE = SQLConst.SITE_USER_GROUP;
	private static SQLiteUserGroupDao instance = new SQLiteUserGroupDao();

	public static SQLiteUserGroupDao getInstance() {
		return instance;
	}

	public int queryGroupMembersCount(String groupId) throws SQLException {
		String sql = "SELECT count(site_user_id) FROM " + USER_GROUP_TABLE + " WHERE site_group_id=?;";
		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, groupId);
		ResultSet rs = preState.executeQuery();
		return rs.getInt(1);
	}

	/**
	 * 添加群组新成员
	 * 
	 * @param createUserId
	 * @param groupId
	 * @param memberIds
	 * @return
	 * @throws SQLException
	 */
	public boolean addGroupMember(String siteUserId, String groupId, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_GROUP_TABLE
				+ "(site_user_id,site_group_id,user_role,add_time) VALUES(?,?,?,?);";
		int result = 0;
		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, groupId);
		preState.setInt(3, GroupProto.GroupMemberRole.OWNER_VALUE);
		preState.setLong(4, System.currentTimeMillis());
		result = preState.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + siteUserId + "," + groupId + "," + status);

		return result > 0;
	}

	public List<SimpleGroupBean> queryUserGroups(String userId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleGroupBean> groupList = new ArrayList<SimpleGroupBean>();

		String sql = "SELECT a.site_group_id,b.group_name,b.group_photo FROM " + USER_GROUP_TABLE + " AS a LEFT JOIN "
				+ SQLConst.SITE_GROUP_PROFILE
				+ " AS b WHERE a.site_group_id=b.site_group_id AND b.group_status>0 AND a.site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userId);

		ResultSet rs = preStatement.executeQuery();

		while (rs.next()) {
			SimpleGroupBean bean = new SimpleGroupBean();
			bean.setGroupId(rs.getString(1));
			bean.setGroupName(rs.getString(2));
			bean.setGroupPhoto(rs.getString(3));

			groupList.add(bean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, groupList.toString(), sql + userId + "," + userId);

		return groupList;
	}

	public List<String> queryUserGroupsId(String userId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> groupsIdList = new ArrayList<String>();

		String sql = "SELECT site_group_id FROM site_user_group WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userId);

		ResultSet rs = preStatement.executeQuery();

		while (rs.next()) {
			String groupId = rs.getString(1);
			groupsIdList.add(groupId);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, groupsIdList.toString(), sql + userId + "," + userId);

		return groupsIdList;
	}

	public List<String> queryGroupMembersId(String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> userIdList = new ArrayList<String>();

		String sql = "SELECT site_user_id FROM " + USER_GROUP_TABLE + " WHERE site_group_id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, groupId);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			String userId = rs.getString(1);
			userIdList.add(userId);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userIdList.toString(), sql + "," + groupId);

		return userIdList;
	}

	public List<GroupMemberBean> queryGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		int startNum = (pageNum - 1) * pageSize;
		String sql = "SELECT a.site_user_id,b.user_name,b.user_photo,a.user_role FROM " + USER_GROUP_TABLE
				+ " AS a LEFT JOIN " + SQLConst.SITE_USER_PROFILE
				+ " AS b WHERE a.site_user_id=b.site_user_id AND a.site_group_id=? limit ?,?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, groupId);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			GroupMemberBean member = new GroupMemberBean();
			member.setUserId(rs.getString(1));
			member.setUserName(rs.getString(2));
			member.setUserPhoto(rs.getString(3));
			member.setUserRole(rs.getInt(4));
			membersList.add(member);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, membersList.toString(), sql + "," + groupId);
		return membersList;
	}

	public List<GroupMemberBean> queryNonGroupMemberList(String groupId, int pageNum, int pageSize)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		int startNum = (pageNum - 1) * pageSize;
		String sql = "SELECT site_user_id,user_name,user_photo FROM " + SQLConst.SITE_USER_PROFILE
				+ " WHERE site_user_id NOT IN (SELECT DISTINCT site_user_id FROM " + SQLConst.SITE_USER_GROUP
				+ " WHERE site_group_id=?) LIMIT ?,?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, groupId);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			GroupMemberBean member = new GroupMemberBean();
			member.setUserId(rs.getString(1));
			member.setUserName(rs.getString(2));
			member.setUserPhoto(rs.getString(3));
			member.setUserRole(GroupProto.GroupMemberRole.NONMEMBER_VALUE);
			membersList.add(member);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, membersList.toString(), sql + "," + groupId);
		return membersList;
	}

	public GroupMemberBean getGroupMember(String siteUserId, String groupId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,user_role FROM " + USER_GROUP_TABLE
				+ " WHERE site_user_id=? AND site_group_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		preStatement.setString(2, groupId);
		ResultSet rs = preStatement.executeQuery();

		GroupMemberBean member = new GroupMemberBean();
		if (rs.next()) {
			member.setUserId(rs.getString(1));
			member.setUserRole(rs.getInt(2));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, member.toString(), sql + siteUserId + "," + groupId);
		return member;
	}

	public boolean deleteGroupMember(String groupId, List<String> userIds) {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_GROUP_TABLE + " WHERE site_user_id=? AND site_group_id=?;";
		int result = 0;
		for (String userId : userIds) {
			try {
				PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
				preState.setString(1, userId);
				preState.setString(2, groupId);
				result += preState.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "=" + userIds.size(), sql);

		return result >= userIds.size();
	}

}
