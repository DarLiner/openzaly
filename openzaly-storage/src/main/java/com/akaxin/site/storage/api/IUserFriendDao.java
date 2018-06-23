package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserFriendBean;

public interface IUserFriendDao {

	public int getUserFriendNum(String siteUserId) throws SQLException;

	public List<SimpleUserBean> getUserFriends(String userId) throws SQLException;

	public List<SimpleUserBean> getUserFriendsByPage(String siteUserId, int pageNum, int pageSize) throws SQLException;

	boolean saveRelation(String siteUserId, String siteFriendId, int relation) throws SQLException;

	int queryRelation(String siteUserId, String siteFriendId) throws SQLException;

	boolean queryIsFriendRelation(String siteUserId, String siteFriendId, boolean isMaster) throws SQLException;

	boolean deleteRelation(String siteUserId, String siteFriendId) throws SQLException;

	boolean updateRelation(String siteUserId, String siteFriendId, int relation) throws SQLException;

	public UserFriendBean getFriendSetting(String siteUserId, String siteFriendId) throws SQLException;

	public boolean updateFriendSetting(String siteUserId, UserFriendBean bean) throws SQLException;

	public boolean isMute(String siteUserId, String siteFriendId) throws SQLException;

	public boolean updateMute(String siteUserId, String siteFriendId, boolean mute) throws SQLException;

	public boolean remarkFriend(String siteUserId, String siteFriendId, String aliasName, String aliasInLatin)
			throws SQLException;

	public int friendNum(long now, int day) throws SQLException;

	boolean delUserFriend(String siteUserId) throws SQLException;

}
