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

import com.akaxin.site.storage.api.IUserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.sqlite.SQLiteUserProfileDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:03
 */
public class UserProfileDaoService implements IUserProfileDao {

	@Override
	public boolean saveUserProfile(UserProfileBean bean) throws SQLException {
		return SQLiteUserProfileDao.getInstance().saveUserProfile(bean);
	}

	@Override
	public String getSiteUserIdByGlobalUserId(String globalUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().querySiteUserIdByGlobalUserId(globalUserId);
	}

	@Override
	public String getSiteUserId(String userIdPubk) throws SQLException {
		return SQLiteUserProfileDao.getInstance().querySiteUserId(userIdPubk);
	}

	@Override
	public String getGlobalUserId(String siteUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryGlobalUserId(siteUserId);
	}

	@Override
	public SimpleUserBean getSimpleProfileById(String siteUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().querySimpleProfileById(siteUserId);
	}

	@Override
	public SimpleUserBean getSimpleProfileByGlobalUserId(String globalUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().querySimpleProfileByGlobalUserId(globalUserId);
	}

	@Override
	public SimpleUserBean getSimpleProfileByPubk(String userIdPubk) throws SQLException {
		return SQLiteUserProfileDao.getInstance().querySimpleProfileByPubk(userIdPubk);
	}

	@Override
	public UserFriendBean getFriendProfileById(String siteUserId, String siteFriendId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryFriendProfileById(siteUserId, siteFriendId);
	}

	@Override
	public UserProfileBean getUserProfileById(String userId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryUserProfileById(userId);
	}

	@Override
	public UserProfileBean getUserProfileByGlobalUserId(String globalUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryUserProfileByGlobalUserId(globalUserId);
	}

	@Override
	public UserProfileBean getUserProfileByPubk(String userIdPubk) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryUserProfileByPubk(userIdPubk);
	}

	@Override
	public int updateUserProfile(UserProfileBean userBean) throws SQLException {
		return SQLiteUserProfileDao.getInstance().updateUserProfile(userBean);
	}

	@Override
	public int updateUserStatus(String siteUserId, int status) throws SQLException {
		return SQLiteUserProfileDao.getInstance().updateUserStatus(siteUserId, status);
	}

	@Override
	public List<SimpleUserRelationBean> getUserRelationPageList(String siteUserId, int pageNum, int pageSize)
			throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryUserRelationPageList(siteUserId, pageNum, pageSize);
	}

	@Override
	public List<SimpleUserBean> getUserPageList(int pageNum, int pageSize) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryUserPageList(pageNum, pageSize);
	}

	@Override
	public boolean isMute(String siteUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryMute(siteUserId);
	}

	@Override
	public boolean updateMute(String siteUserId, boolean mute) throws SQLException {
		return SQLiteUserProfileDao.getInstance().updateMute(siteUserId, mute);
	}

	@Override
	public int queryNumRegisterPerDay(long now, int day) throws SQLException {
		return SQLiteUserProfileDao.getInstance().queryNumRegisterPerDay(now, day);
	}

	@Override
	public int getUserNum(long now, int day) throws SQLException {
		return SQLiteUserProfileDao.getInstance().getUserNum(now, day);
	}

	@Override
	public boolean delUser(String siteUserId) throws SQLException {
		return SQLiteUserProfileDao.getInstance().delUser(siteUserId);
	}

}
