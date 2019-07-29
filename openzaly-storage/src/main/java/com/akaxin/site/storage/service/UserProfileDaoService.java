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
import com.akaxin.site.storage.dao.SiteUserProfileDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:03
 */
public class UserProfileDaoService implements IUserProfileDao {

	@Override
	public boolean saveProfile(UserProfileBean bean) throws SQLException {
		return SiteUserProfileDao.getInstance().saveProfile(bean);
	}

	@Override
	public String getSiteUserIdByGlobalUserId(String globalUserId) throws SQLException {
		return SiteUserProfileDao.getInstance().querySiteUserIdByGlobalUserId(globalUserId);
	}

	@Override
	public String getSiteUserIdByPhone(String phoneId) throws SQLException {
		return SiteUserProfileDao.getInstance().querySiteUserIdPhone(phoneId);
	}

	@Override
	public String getSiteUserIdByLowercaseLoginId(String lowercaseLoginId) throws SQLException {
		return SiteUserProfileDao.getInstance().querySiteUserIdByLowercaseLoginId(lowercaseLoginId);
	}

	@Override
	public String getGlobalUserIdBySiteUserId(String siteUserId) throws SQLException {
		return SiteUserProfileDao.getInstance().queryGlobalUserIdBySiteUserId(siteUserId);
	}

	@Override
	public String getSiteLoginIdBySiteUserId(String siteUserId) throws SQLException {
		return SiteUserProfileDao.getInstance().querySiteLoginIdBySiteUserId(siteUserId);
	}

	@Override
	public SimpleUserBean getSimpleProfileById(String siteUserId, boolean isMaster) throws SQLException {
		return SiteUserProfileDao.getInstance().querySimpleProfileById(siteUserId, isMaster);
	}

	@Override
	public SimpleUserBean getSimpleProfileByGlobalUserId(String globalUserId, boolean isMaster) throws SQLException {
		return SiteUserProfileDao.getInstance().querySimpleProfileByGlobalUserId(globalUserId, isMaster);
	}

	@Override
	public UserFriendBean getFriendProfileById(String siteUserId, String siteFriendId) throws SQLException {
		return SiteUserProfileDao.getInstance().queryFriendProfileById(siteUserId, siteFriendId);
	}

	@Override
	public UserProfileBean getUserProfileById(String userId) throws SQLException {
		return SiteUserProfileDao.getInstance().queryUserProfileById(userId);
	}

	@Override
	public UserProfileBean getUserProfileByGlobalUserId(String globalUserId) throws SQLException {
		return SiteUserProfileDao.getInstance().queryUserProfileByGlobalUserId(globalUserId);
	}

	@Override
	public UserProfileBean getUserProfileByFullPhoneId(String fullPhoneId) throws SQLException {
		return SiteUserProfileDao.getInstance().queryUserProfileByFullPhoneId(fullPhoneId);
	}

	@Override
	public int updateProfile(UserProfileBean userBean) throws SQLException {
		return SiteUserProfileDao.getInstance().updateUserProfile(userBean);
	}

	@Override
	public int updateUserIdPubk(String siteUserId, String globalUserId, String userIdPubk) throws SQLException {
		return SiteUserProfileDao.getInstance().updateUserIdPubk(siteUserId, globalUserId, userIdPubk);
	}

	@Override
	public int updateUserStatus(String siteUserId, int status) throws SQLException {
		return SiteUserProfileDao.getInstance().updateUserStatus(siteUserId, status);
	}

	@Override
	public List<SimpleUserRelationBean> getUserRelationPageList(String siteUserId, int pageNum, int pageSize)
			throws SQLException {
		return SiteUserProfileDao.getInstance().queryUserRelationPageList(siteUserId, pageNum, pageSize);
	}

	@Override
	public int getTotalUserNum() throws SQLException {
		return SiteUserProfileDao.getInstance().queryTotalUserNum();
	}

	@Override
	public List<SimpleUserBean> getUserPageList(int pageNum, int pageSize) throws SQLException {
		return SiteUserProfileDao.getInstance().queryUserPageList(pageNum, pageSize);
	}

	@Override
	public boolean isMute(String siteUserId) throws SQLException {
		return SiteUserProfileDao.getInstance().queryMute(siteUserId);
	}

	@Override
	public boolean updateMute(String siteUserId, boolean mute) throws SQLException {
		return SiteUserProfileDao.getInstance().updateMute(siteUserId, mute);
	}

	@Override
	public int queryRegisterNumPerDay(long now, int day) throws SQLException {
		return SiteUserProfileDao.getInstance().queryRegisterNumPerDay(now, day);
	}

	@Override
	public int getUserNum(long now, int day) throws SQLException {
		return SiteUserProfileDao.getInstance().getUserNum(now, day);
	}

	@Override
	public boolean delUser(String siteUserId) throws SQLException {
		return SiteUserProfileDao.getInstance().delUser(siteUserId);
	}

}
