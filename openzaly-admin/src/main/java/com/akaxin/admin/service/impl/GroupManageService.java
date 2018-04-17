package com.akaxin.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akaxin.admin.service.IGroupService;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-17 18:59:24
 */
@Service("groupManageService")
public class GroupManageService implements IGroupService {

	@Override
	public GroupProfileBean getGroupProfile(String siteGroupId) {
		return UserGroupDao.getInstance().getGroupProfile(siteGroupId);
	}

	@Override
	public boolean updateGroupProfile(GroupProfileBean bean) {
		return UserGroupDao.getInstance().updateGroupProfile(bean);
	}

	@Override
	public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) {
		return UserGroupDao.getInstance().getGroupList(pageNum, pageSize);
	}

	@Override
	public List<GroupMemberBean> getGroupMembers(String siteGroupId, int pageNum, int pageSize) {
		return UserGroupDao.getInstance().getGroupMemberList(siteGroupId, pageNum, pageSize);
	}

	@Override
	public List<GroupMemberBean> getNonGroupMembers(String siteGroupId, int pageNum, int pageSize) {
		return UserGroupDao.getInstance().getNonGroupMemberList(siteGroupId, pageNum, pageSize);
	}

	@Override
	public boolean addGroupMembers(String siteGroupId, List<String> newMemberList) {
		return UserGroupDao.getInstance().addGroupMember(null, siteGroupId, newMemberList);
	}

	@Override
	public boolean removeGroupMembers(String siteGroupId, List<String> groupMemberList) {
		return UserGroupDao.getInstance().deleteGroupMember(siteGroupId, groupMemberList);
	}

	@Override
	public boolean dismissGroup(String siteGroupId) {
		return UserGroupDao.getInstance().deleteGroup(siteGroupId);
	}

}
