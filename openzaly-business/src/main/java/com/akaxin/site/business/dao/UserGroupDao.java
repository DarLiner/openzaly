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
package com.akaxin.site.business.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.GroupProto;
import com.akaxin.site.business.impl.notice.GroupNotice;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 群组相关数据源操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-10-20 17:30:14
 */
public class UserGroupDao {
	private static final Logger logger = LoggerFactory.getLogger(UserGroupDao.class);

	private IGroupDao groupDao = new GroupDaoService();
	private IMessageDao messageDao = new MessageDaoService();

	private UserGroupDao() {

	}

	static class SingletonHolder {
		private static UserGroupDao instance = new UserGroupDao();
	}

	public static UserGroupDao getInstance() {
		return SingletonHolder.instance;
	}

	public String getGroupMaster(String groupId) {
		try {
			return groupDao.getGroupOwner(groupId);
		} catch (Exception e) {
			logger.error("get group master error.", e);
		}
		return null;
	}

	public List<SimpleGroupBean> getUserGroups(String userId) {
		List<SimpleGroupBean> goupList = new ArrayList<SimpleGroupBean>();
		try {
			goupList = groupDao.getUserGroups(userId);
		} catch (Exception e) {
			logger.error("get user group list error.", e);
		}
		return goupList;
	}

	public List<GroupMemberBean> getGroupMemberList(String groupId, int pageNum, int pageSize) {
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		try {
			membersList = groupDao.getGroupMemberList(groupId, pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get group members error.", e);
		}
		return membersList;
	}

	public List<GroupMemberBean> getNonGroupMemberList(String groupId, int pageNum, int pageSize) {
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		try {
			membersList = groupDao.getNonGroupMemberList(groupId, pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get group members error.", e);
		}
		return membersList;
	}

	public int getGroupMemberCount(String groupId) {
		try {
			return groupDao.getGroupMembersCount(groupId);
		} catch (SQLException e) {
			logger.error("get group member count error.", e);
		}
		return 0;
	}

	// 创建群
	public GroupProfileBean createGroup(String createUserId, String groupName, List<String> userIds) {
		GroupProfileBean bean = new GroupProfileBean();
		try {
			bean.setCreateTime(System.currentTimeMillis());
			bean.setGroupName(groupName);
			bean.setCreateUserId(createUserId);
			// 群头像使用默认
			// bean.setGroupPhoto(GROUP_DEFAULT_ICON);
			bean.setGroupStatus(GroupProto.GroupStatus.GROUP_NORMAL_VALUE);
			// 1.创建群资料
			bean = groupDao.addGroupProfile(bean);
			for (String memberId : userIds) {
				int status = GroupProto.GroupMemberRole.MEMBER_VALUE;
				if (createUserId.equals(memberId)) {
					status = GroupProto.GroupMemberRole.OWNER_VALUE;
				}
				groupDao.addGroupMember(memberId, bean.getGroupId(), status);
			}

		} catch (Exception e) {
			logger.error("create group error.", e);
		}
		return bean;
	}

	public boolean addGroupMember(String siteUserId, String groupId, List<String> userIdList) {
		try {
			// 1.获取当前，群消息最大游标
			long maxPointer = messageDao.queryMaxGroupPointer(groupId);
			logger.info("add group member,maxPointer={}", maxPointer);
			// 2.设置个人默认游标
			// 3.增加群成员
			for (String memberId : userIdList) {
				messageDao.updateGroupPointer(groupId, memberId, null, maxPointer);
				int status = GroupProto.GroupMemberRole.MEMBER_VALUE;
				groupDao.addGroupMember(memberId, groupId, status);
			}

			return true;
		} catch (Exception e) {
			logger.error("add group member error.", e);
		} finally {
			// 添加完用户，向群消息中添加GroupMsgNotice
			new GroupNotice().userAddGroupNotice(siteUserId, groupId, userIdList);
		}
		return false;
	}

	public GroupProfileBean getGroupProfile(String groupId) {
		GroupProfileBean profileBean = null;
		try {
			profileBean = groupDao.queryGroupProfile(groupId);
		} catch (SQLException e) {
			logger.error("get group profile error.", e);
		}
		return profileBean;
	}

	public boolean updateGroupProfile(GroupProfileBean gprofileBean) {
		int result = 0;
		try {
			result = groupDao.updateGroupProfile(gprofileBean);
		} catch (SQLException e) {
			logger.error("update group profile error.");
		}
		return result > 0;
	}

	public boolean deleteGroup(String groupId) {
		try {
			return groupDao.deleteGroupProfile(groupId);
		} catch (Exception e) {
			logger.error("delete group error.", e);
		}
		return false;
	}

	public boolean deleteGroupMember(String groupId, List<String> userIds) {
		return groupDao.deleteGroupMember(groupId, userIds);
	}

	public boolean quitGroup(String groupId, String userId) {
		return groupDao.deleteGroupMember(groupId, Arrays.asList(userId));
	}

	public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) {
		List<SimpleGroupBean> goupList = null;
		try {
			goupList = groupDao.getGroupList(pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get user group list error.", e);
		}
		return goupList;
	}

	public boolean updateGroupOwner(String siteUserId, String groupId) {
		try {
			return groupDao.updateGroupOwner(siteUserId, groupId) > 0;
		} catch (SQLException e) {
			logger.error("update group owner error");
		}
		return false;
	}

}
