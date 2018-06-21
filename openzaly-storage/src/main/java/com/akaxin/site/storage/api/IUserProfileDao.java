package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.bean.UserProfileBean;

public interface IUserProfileDao {

	public boolean saveProfile(UserProfileBean bean) throws SQLException;

	public String getSiteUserIdByGlobalUserId(String globalUserId) throws SQLException;

	public String getSiteUserIdByPhone(String phoneId) throws SQLException;

	public String getSiteUserIdByLowercaseLoginId(String lowercaseLoginId) throws SQLException;

	public String getGlobalUserIdBySiteUserId(String siteUserId) throws SQLException;

	public String getSiteLoginIdBySiteUserId(String siteUserId) throws SQLException;

	public SimpleUserBean getSimpleProfileById(String userId) throws SQLException;

	public SimpleUserBean getSimpleProfileByGlobalUserId(String globalUserId) throws SQLException;

	public UserFriendBean getFriendProfileById(String siteUserId, String siteFriend) throws SQLException;

	public UserProfileBean getUserProfileById(String siteUserId) throws SQLException;

	public UserProfileBean getUserProfileByGlobalUserId(String userId) throws SQLException;

	public int updateProfile(UserProfileBean userBean) throws SQLException;

	public int updateUserStatus(String siteUserId, int status) throws SQLException;

	public List<SimpleUserRelationBean> getUserRelationPageList(String siteUserId, int pageNum, int pageSize)
			throws SQLException;

	int getTotalUserNum() throws SQLException;

	public List<SimpleUserBean> getUserPageList(int pageNum, int pageSize) throws SQLException;

	public boolean isMute(String userId) throws SQLException;

	public boolean updateMute(String userId, boolean mute) throws SQLException;

	public int queryRegisterNumPerDay(long now, int day) throws SQLException;

	int getUserNum(long now, int day) throws SQLException;

	boolean delUser(String siteUserId) throws SQLException;
}
