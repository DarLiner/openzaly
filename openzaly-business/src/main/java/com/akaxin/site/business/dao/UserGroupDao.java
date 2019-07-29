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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.GroupProto;
import com.akaxin.site.business.impl.notice.GroupNotice;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.api.IUserGroupDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserGroupBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.akaxin.site.storage.service.MessageDaoService;
import com.akaxin.site.storage.service.UserGroupDaoService;

/**
 * 用户群组群组相关数据源操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-10-20 17:30:14
 */
public class UserGroupDao {
	private static final Logger logger = LoggerFactory.getLogger(UserGroupDao.class);

	private IGroupDao groupDao = new GroupDaoService();
	private IMessageDao messageDao = new MessageDaoService();
	private IUserGroupDao userGroupDao = new UserGroupDaoService();

	private UserGroupDao() {

	}

	static class SingletonHolder {
		private static UserGroupDao instance = new UserGroupDao();
	}

	public static UserGroupDao getInstance() {
		return SingletonHolder.instance;
	}

	// 获取群主
	public String getGroupMaster(String groupId) {
		try {
			return groupDao.getGroupOwner(groupId);
		} catch (Exception e) {
			logger.error("get group master error.", e);
		}
		return null;
	}

	public int getUserGroupCount(String siteUserId) {
		try {
			return groupDao.getUserGroupCount(siteUserId);
		} catch (Exception e) {
			logger.error("get user group num error.", e);
		}
		return -1;
	}

	public List<SimpleGroupBean> getUserGroupList(String siteUserId) {
		List<SimpleGroupBean> goupList = new ArrayList<SimpleGroupBean>();
		try {
			goupList = groupDao.getUserGroupList(siteUserId);
		} catch (Exception e) {
			logger.error("get user group list error.", e);
		}
		return goupList;
	}

	public List<SimpleGroupBean> getUserGroupList(String siteUserId, int pageNum, int pageSize) {
		List<SimpleGroupBean> goupList = null;
		try {
			goupList = groupDao.getUserGroupList(siteUserId, pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get user group list by page error.", e);
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

	// 管理后台使用
	public int getNonGroupMemberNum(String groupId) {
		try {
			return groupDao.getNonGroupMemberNum(groupId);
		} catch (Exception e) {
			logger.error("get non group members error.", e);
		}
		return 0;
	}

	// 管理后台使用
	public List<GroupMemberBean> getNonGroupMemberList(String groupId, int pageNum, int pageSize) {
		List<GroupMemberBean> membersList = new ArrayList<GroupMemberBean>();
		try {
			membersList = groupDao.getNonGroupMemberList(groupId, pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get non group members error.", e);
		}
		return membersList;
	}

	public int getUserFriendNonGroupMemberNum(String siteUserId, String groupId) {
		try {
			return groupDao.getUserFriendNonGroupMemberNum(siteUserId, groupId);
		} catch (Exception e) {
			logger.error("get user friend non group member number error.", e);
		}
		return 0;
	}

	public List<SimpleUserBean> getUserFriendNonGroupMemberList(String siteUserId, String groupId, int pageNum,
			int pageSize) {
		List<SimpleUserBean> userList = new ArrayList<SimpleUserBean>();
		try {
			userList = groupDao.getUserFriendNonGroupMemberList(siteUserId, groupId, pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get user friend non group members error.", e);
		}
		return userList;
	}

	// 获取群成员人数
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
			// 2.添加群成员入库
			if (bean != null) {
				for (String memberId : userIds) {
					int status = GroupProto.GroupMemberRole.MEMBER_VALUE;
					if (createUserId.equals(memberId)) {
						status = GroupProto.GroupMemberRole.OWNER_VALUE;
					}
					groupDao.addGroupMember(memberId, bean.getGroupId(), status);
				}
				// 3.群消息中发送通知
				new GroupNotice().addGroupMemberNotice(createUserId, bean.getGroupId(), userIds);
			}
		} catch (Exception e) {
			// 事务回滚，删除操作
			// 删除1.群资料
			// 删除2.群成员
			logger.error("create group error.", e);
		}
		return bean;
	}

	public boolean addGroupMember(String siteUserId, String groupId, List<String> userIdList) {
		try {
			// 1.获取当前，群消息最大游标
			long maxPointer = messageDao.queryMaxGroupPointer(groupId);
			// 2.设置个人默认游标
			// 3.增加群成员
			for (String memberId : userIdList) {
				messageDao.updateGroupPointer(groupId, memberId, null, maxPointer);
				int status = GroupProto.GroupMemberRole.MEMBER_VALUE;
				if (!groupDao.addGroupMember(memberId, groupId, status)) {
					return false;
				}
			}
			// 添加完用户，向群消息中添加GroupMsgNotice
			new GroupNotice().addGroupMemberNotice(siteUserId, groupId, userIdList);
			return true;
		} catch (Exception e) {
			logger.error("add group member error.", e);
		}
		return false;
	}

	public boolean addDefaultGroupMember(String defaultGroupId, String siteUserId) {
		try {
			if (addGroupMember(defaultGroupId, siteUserId)) {
				// 添加完用户，向群消息中添加GroupMsgNotice
				new GroupNotice().addDefaultGroupMemberNotice(defaultGroupId, siteUserId);
				return true;
			}
		} catch (Exception e) {
			logger.error("add group member error.", e);
		}
		return false;
	}

	public boolean addGroupMemberByToken(String siteGroupId, String siteUserId) {
		try {
			if (addGroupMember(siteGroupId, siteUserId)) {
				// 添加完用户，向群消息中添加GroupMsgNotice
				new GroupNotice().addGroupMemberByTokenNotice(siteGroupId, siteUserId);
				return true;
			}
		} catch (Exception e) {
			logger.error("add group member by token error.", e);
		}
		return false;
	}

	private boolean addGroupMember(String siteGroupId, String siteUserId) throws SQLException {
		// 1.获取当前，群消息最大游标
		long maxPointer = messageDao.queryMaxGroupPointer(siteGroupId);
		// 2.设置个人默认游标
		// 3.增加群成员
		messageDao.updateGroupPointer(siteGroupId, siteUserId, null, maxPointer);
		int status = GroupProto.GroupMemberRole.MEMBER_VALUE;
		return groupDao.addGroupMember(siteUserId, siteGroupId, status);
	}

	public boolean isGroupMember(String siteUserId, String groupId) {
		try {
			GroupMemberBean bean = groupDao.getGroupMember(siteUserId, groupId);
			if (bean != null && StringUtils.isNotEmpty(bean.getUserId())) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("is group member error.", e);
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

	public int getGroupStatus(String siteGroupId) {
		try {
			return groupDao.getGroupStatus(siteGroupId);
		} catch (SQLException e) {
			logger.error(StringHelper.format("get group={} status error", siteGroupId), e);
		}
		return 0;// 默认不可用的群组
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

	public boolean updateGroupIGC(GroupProfileBean bean) {
		int result = 0;
		try {
			result = groupDao.updateGroupIGC(bean);
		} catch (Exception e) {
			logger.error(StringHelper.format("update group profile error.bean={}", bean), e);
		}
		return result > 0;
	}

	public boolean deleteGroup(String groupId) {
		try {
			return groupDao.deleteGroupProfile(groupId);
		} catch (Exception e) {
			logger.error("delete group error,groupId=" + groupId, e);
		}
		return false;
	}

	public boolean deleteGroupMember(String groupId, List<String> userIds) {
		try {
			return groupDao.deleteGroupMember(groupId, userIds);
		} catch (SQLException e) {
			logger.error("delete group member error", e);
		}
		return false;
	}

	public List<String> checkGroupMember(String siteGroupId, List<String> userIds) throws SQLException {
		return userGroupDao.checkGroupMember(siteGroupId, userIds);
	}

	public boolean quitGroup(String groupId, String userId) {
		try {
			return groupDao.deleteGroupMember(groupId, Arrays.asList(userId));
		} catch (SQLException e) {
			logger.error("delete group member error", e);
		}
		return false;
	}

	public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) {
		List<SimpleGroupBean> goupList = null;
		try {
			goupList = groupDao.getGroupList(pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get group list error.", e);
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

	public UserGroupBean getUserGroupSetting(String siteUserId, String siteGroupId) {
		try {
			UserGroupBean bean = userGroupDao.getUserGroupSetting(siteUserId, siteGroupId);
			if (bean == null) {
				bean = new UserGroupBean();
				bean.setMute(false);
			}
			return bean;
		} catch (SQLException e) {
			logger.error("get user group setting error", e);
		}
		return null;
	}

	public boolean updateUserGroupSetting(String siteUserId, UserGroupBean bean) {
		try {
			return userGroupDao.updateUserGroupSetting(siteUserId, bean);
		} catch (SQLException e) {
			logger.error("update user group setting error", e);
		}
		return false;
	}

	public boolean getUserGroupMute(String siteUserId, String siteGroupId) {
		try {
			return userGroupDao.isMute(siteUserId, siteGroupId);
		} catch (SQLException e) {
			logger.error("get user group setting error", e);
		}
		return true;
	}

	public boolean updateUserGroupMute(String siteUserId, UserGroupBean bean) {
		try {
			return userGroupDao.updateUserGroupSetting(siteUserId, bean);
		} catch (SQLException e) {
			logger.error("update user group setting error", e);
		}
		return false;
	}

	public int getTotalGroupNum() {
		try {
			return groupDao.getTotalGroupNum();
		} catch (SQLException e) {
			logger.error("get total group num error", e);
		}
		return 0;
	}

	public GroupProfileBean getSimpleGroupBeanById(String groupId) {
		try {
			return groupDao.querySimpleGroupProfile(groupId);
		} catch (SQLException e) {
			logger.error("get SimpleGroupBean error", e);
		}
		return null;
	}

}
