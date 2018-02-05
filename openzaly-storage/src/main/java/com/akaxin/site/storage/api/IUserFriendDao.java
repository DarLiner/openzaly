package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.SimpleUserBean;

public interface IUserFriendDao {

	boolean saveRelation(String siteUserId, String siteFriendId, int relation) throws SQLException;

	int queryRelation(String siteUserId, String siteFriendId) throws SQLException;

	boolean deleteRelation(String siteUserId, String siteFriendId) throws SQLException;

	boolean updateRelation(String siteUserId, String siteFriendId, int relation) throws SQLException;

	public List<SimpleUserBean> getUserFriends(String userId) throws SQLException;

}
