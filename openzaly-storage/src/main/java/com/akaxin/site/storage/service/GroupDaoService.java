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
package com.akaxin.site.storage.service;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.sqlite.SQLiteGroupProfileDao;
import com.akaxin.site.storage.sqlite.SQLiteUserGroupDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:41
 */
public class GroupDaoService implements IGroupDao {

	@Override
	public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().queryGroupList(pageNum, pageSize);
	}

	@Override
	public String getGroupOwner(String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().getGrouMaster(groupId);
	}

	@Override
	public GroupMemberBean getGroupMember(String siteUserId, String groupId) throws SQLException {
		return SQLiteUserGroupDao.getInstance().getGroupMember(siteUserId, groupId);
	}

	@Override
	public List<String> getGroupMembersId(String groupId) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryGroupMembersId(groupId);
	}

	@Override
	public List<GroupMemberBean> getGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryGroupMemberList(groupId, pageNum, pageSize);
	}

	@Override
	public List<GroupMemberBean> getNonGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryNonGroupMemberList(groupId, pageNum, pageSize);
	}

	@Override
	public List<SimpleUserBean> getUserFriendNonGroupMemberList(String siteUserId, String groupId, int pageNum,
			int pageSize) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryUserFriendNonGroupMemberList(siteUserId, groupId, pageNum,
				pageSize);
	}

	@Override
	public GroupProfileBean addGroupProfile(GroupProfileBean bean) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().saveGroupProfile(bean);
	}

	@Override
	public boolean addGroupMember(String siteUserId, String groupId, int status) throws SQLException {
		return SQLiteUserGroupDao.getInstance().addGroupMember(siteUserId, groupId, status);
	}

	@Override
	public boolean deleteGroupProfile(String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().deleteGroupProfile(groupId);
	}

	@Override
	public int updateGroupProfile(GroupProfileBean bean) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().updateGroupProfile(bean);
	}

	@Override
	public int updateGroupIGC(GroupProfileBean bean) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().updateGroupIGC(bean);
	}

	@Override
	public int getGroupNum(long now, int day) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().getGroupNum(now, day);
	}

	@Override
	public boolean rmGroupProfile(String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().rmGroupProfile(groupId);
	}
	@Override
	public int updateGroupOwner(String siteUserId, String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().updateGroupOwer(siteUserId, groupId);
	}

	@Override
	public GroupProfileBean queryGroupProfile(String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().queryGroupProfile(groupId);
	}

	@Override
	public GroupProfileBean querySimpleGroupProfile(String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().querySimpleGroupProfile(groupId);
	}

	@Override
	public int getGroupStatus(String groupId) throws SQLException {
		return SQLiteGroupProfileDao.getInstance().queryGroupStatus(groupId);
	}

	@Override
	public int getGroupMembersCount(String groupId) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryGroupMembersCount(groupId);
	}

	@Override
	public List<SimpleGroupBean> getUserGroups(String userId) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryUserGroups(userId);
	}

	@Override
	public List<String> getUserGroupsId(String userId) throws SQLException {
		return SQLiteUserGroupDao.getInstance().queryUserGroupsId(userId);
	}

	@Override
	public boolean deleteGroupMember(String groupId, List<String> userIds) {
		return SQLiteUserGroupDao.getInstance().deleteGroupMember(groupId, userIds);
	}

}
