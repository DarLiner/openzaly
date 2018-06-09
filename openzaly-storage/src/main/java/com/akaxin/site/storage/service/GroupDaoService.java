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
import com.akaxin.site.storage.dao.SiteGroupProfileDao;
import com.akaxin.site.storage.dao.SiteUserGroupDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:41
 */
public class GroupDaoService implements IGroupDao {

	@Override
	public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) throws SQLException {
		return SiteGroupProfileDao.getInstance().queryGroupList(pageNum, pageSize);
	}

	@Override
	public List<SimpleGroupBean> getUserGroupList(String userId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryUserGroupList(userId);
	}

	@Override
	public List<SimpleGroupBean> getUserGroupList(String siteUserId, int pageNum, int pageSize) throws SQLException {
		return SiteUserGroupDao.getInstance().queryUserGroupList(siteUserId, pageNum, pageSize);
	}

	@Override
	public int getUserGroupCount(String siteUserId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryUserGroupCount(siteUserId);
	}

	@Override
	public String getGroupOwner(String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().getGrouMaster(groupId);
	}

	@Override
	public GroupMemberBean getGroupMember(String siteUserId, String groupId) throws SQLException {
		return SiteUserGroupDao.getInstance().getGroupMember(siteUserId, groupId);
	}

	@Override
	public List<String> getGroupMembersId(String groupId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryGroupMembersId(groupId);
	}

	@Override
	public List<GroupMemberBean> getGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException {
		return SiteUserGroupDao.getInstance().queryGroupMemberList(groupId, pageNum, pageSize);
	}

	@Override
	public int getNonGroupMemberNum(String groupId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryNonGroupMemberNum(groupId);
	}

	@Override
	public List<GroupMemberBean> getNonGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException {
		return SiteUserGroupDao.getInstance().queryNonGroupMemberList(groupId, pageNum, pageSize);
	}

	@Override
	public int getUserFriendNonGroupMemberNum(String siteUserId, String groupId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryUserFriendNonGroupMemberNum(siteUserId, groupId);
	}

	@Override
	public List<SimpleUserBean> getUserFriendNonGroupMemberList(String siteUserId, String groupId, int pageNum,
			int pageSize) throws SQLException {
		return SiteUserGroupDao.getInstance().queryUserFriendNonGroupMemberList(siteUserId, groupId, pageNum,
				pageSize);
	}

	@Override
	public GroupProfileBean addGroupProfile(GroupProfileBean bean) throws SQLException {
		return SiteGroupProfileDao.getInstance().saveGroupProfile(bean);
	}

	@Override
	public boolean addGroupMember(String siteUserId, String groupId, int status) throws SQLException {
		return SiteUserGroupDao.getInstance().addGroupMember(siteUserId, groupId, status);
	}

	@Override
	public boolean deleteGroupProfile(String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().deleteGroupProfile(groupId);
	}

	@Override
	public int updateGroupProfile(GroupProfileBean bean) throws SQLException {
		return SiteGroupProfileDao.getInstance().updateGroupProfile(bean);
	}

	@Override
	public int updateGroupIGC(GroupProfileBean bean) throws SQLException {
		return SiteGroupProfileDao.getInstance().updateGroupIGC(bean);
	}

	@Override
	public int getTotalGroupNum() throws SQLException {
		return SiteGroupProfileDao.getInstance().getTotalGroupNum();
	}

	@Override
	public int getGroupNum(long now, int day) throws SQLException {
		return SiteGroupProfileDao.getInstance().getGroupNum(now, day);
	}

	@Override
	public boolean rmGroupProfile(String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().rmGroupProfile(groupId);
	}

	@Override
	public int updateGroupOwner(String siteUserId, String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().updateGroupOwer(siteUserId, groupId);
	}

	@Override
	public GroupProfileBean queryGroupProfile(String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().queryGroupProfile(groupId);
	}

	@Override
	public GroupProfileBean querySimpleGroupProfile(String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().querySimpleGroupProfile(groupId);
	}

	@Override
	public int getGroupStatus(String groupId) throws SQLException {
		return SiteGroupProfileDao.getInstance().queryGroupStatus(groupId);
	}

	@Override
	public int getGroupMembersCount(String groupId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryGroupMembersCount(groupId);
	}

	@Override
	public List<String> getUserGroupsId(String userId) throws SQLException {
		return SiteUserGroupDao.getInstance().queryUserGroupsId(userId);
	}

	@Override
	public boolean deleteGroupMember(String groupId, List<String> userIds) throws SQLException {
		return SiteUserGroupDao.getInstance().deleteGroupMember(groupId, userIds);
	}

}
