package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;

public interface IGroupDao {

	int getUserGroupCount(String siteUserId) throws SQLException;

	public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) throws SQLException;

	public List<SimpleGroupBean> getUserGroupList(String userId) throws SQLException;

	List<SimpleGroupBean> getUserGroupList(String siteUserId, int pageNum, int pageSize) throws SQLException;

	public String getGroupOwner(String groupId) throws SQLException;

	public int getGroupMembersCount(String groupId) throws SQLException;

	public List<String> getGroupMembersId(String groupId) throws SQLException;

	public GroupProfileBean addGroupProfile(GroupProfileBean bean) throws SQLException;

	public boolean addGroupMember(String siteUserId, String groupId, int status) throws SQLException;

	boolean deleteGroupProfile(String groupId) throws SQLException;

	public int updateGroupProfile(GroupProfileBean bean) throws SQLException;

	int updateGroupOwner(String siteUserId, String groupid) throws SQLException;

	public GroupProfileBean queryGroupProfile(String groupId) throws SQLException;

	public GroupProfileBean querySimpleGroupProfile(String groupId) throws SQLException;

	public int getGroupStatus(String groupId) throws SQLException;

	public List<String> getUserGroupsId(String userId) throws SQLException;

	public GroupMemberBean getGroupMember(String siteUserId, String groupId) throws SQLException;

	public List<GroupMemberBean> getGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException;

	public boolean deleteGroupMember(String groupId, List<String> userIds);

	public List<GroupMemberBean> getNonGroupMemberList(String groupId, int pageNum, int pageSize) throws SQLException;

	public List<SimpleUserBean> getUserFriendNonGroupMemberList(String siteUserId, String groupId, int pageNum,
			int pageSize) throws SQLException;

	public int updateGroupIGC(GroupProfileBean bean) throws SQLException;

	int getGroupNum(long now, int day) throws SQLException;

	boolean rmGroupProfile(String groupId) throws SQLException;

}
