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

import com.akaxin.site.storage.api.IUserFriendDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.sqlite.SQLiteUserFriendDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:10
 */
public class UserFriendDaoService implements IUserFriendDao {

	@Override
	public int getUserFriendNum(String siteUserId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryUserFriendNum(siteUserId);
	}

	@Override
	public List<SimpleUserBean> getUserFriends(String siteUserId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryUserFriends(siteUserId);
	}

	@Override
	public List<SimpleUserBean> getUserFriendsByPage(String siteUserId, int pageNum, int pageSize) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryUserFriendsByPage(siteUserId, pageNum, pageSize);
	}

	@Override
	public boolean saveRelation(String siteUserId, String siteFriendId, int relation) throws SQLException {
		return SQLiteUserFriendDao.getInstance().saveRelation(siteUserId, siteFriendId, relation);
	}

	@Override
	public int queryRelation(String siteUserId, String siteFriendId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryRelation(siteUserId, siteFriendId);
	}

	@Override
	public boolean queryIsFriendRelation(String siteUserId, String siteFriendId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryIsFriendRelation(siteUserId, siteFriendId);
	}

	@Override
	public boolean updateRelation(String siteUserId, String siteFriendId, int relation) throws SQLException {
		return SQLiteUserFriendDao.getInstance().updateRelation(siteUserId, siteFriendId, relation);
	}

	@Override
	public boolean deleteRelation(String siteUserId, String siteFriendId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().deleteRelation(siteUserId, siteFriendId);
	}

	@Override
	public UserFriendBean getFriendSetting(String siteUserId, String siteFriendId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryUserFriendSetting(siteUserId, siteFriendId);
	}

	@Override
	public boolean updateFriendSetting(String siteUserId, UserFriendBean bean) throws SQLException {
		return SQLiteUserFriendDao.getInstance().updateUserFriendSetting(siteUserId, bean);
	}

	@Override
	public boolean isMute(String siteUserId, String siteFriendId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().queryMute(siteUserId, siteFriendId);
	}

	@Override
	public boolean updateMute(String siteUserId, String siteFriendId, boolean mute) throws SQLException {
		return SQLiteUserFriendDao.getInstance().updateMute(siteUserId, siteFriendId, mute);
	}

	@Override
	public boolean remarkFriend(String siteUserId, String siteFriendId, String aliasName, String aliasNameInLatin)
			throws SQLException {
		return SQLiteUserFriendDao.getInstance().updateFriendAlias(siteUserId, siteFriendId, aliasName,
				aliasNameInLatin);
	}

	@Override
	public int friendNum(long now, int day) throws SQLException {
		return SQLiteUserFriendDao.getInstance().getFrienNum(now, day);
	}

	@Override
	public boolean delUserFriend(String siteUserId) throws SQLException {
		return SQLiteUserFriendDao.getInstance().delUserFriend(siteUserId);
	}

}
