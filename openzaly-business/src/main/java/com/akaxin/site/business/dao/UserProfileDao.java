/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.business.dao;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.service.UserProfileDaoService;

/**
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-09 20:44:21
 */
public class UserProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(UserProfileDao.class);
	private static UserProfileDao instance = new UserProfileDao();
	private IUserProfileDao userProfileDao = new UserProfileDaoService();

	public static UserProfileDao getInstance() {
		return instance;
	}

	public String getSiteUserIdByGlobalUserId(String globalUserId) {
		try {
			return userProfileDao.getSiteUserIdByGlobalUserId(globalUserId);
		} catch (SQLException e) {
			logger.error("get siteUserId by globalUserId error.", e);
		}
		return null;
	}

	public String getSiteUserIdByPhone(String phoneId) {
		try {
			return userProfileDao.getSiteUserIdByPhone(phoneId);
		} catch (SQLException e) {
			logger.error("get siteUserId by phone error.", e);
		}
		return null;
	}

	public String getSiteUserIdByLoginId(String lowercaseLoginId) {
		try {
			return userProfileDao.getSiteUserIdByLowercaseLoginId(lowercaseLoginId);
		} catch (SQLException e) {
			logger.error("get siteUserId by lowercase siteLoginId error.", e);
		}
		return null;
	}

	public String getSiteLoginIdBySiteUserId(String siteUserId) {
		try {
			return userProfileDao.getSiteLoginIdBySiteUserId(siteUserId);
		} catch (SQLException e) {
			logger.error("get siteLoginId by siteUserId error.", e);
		}
		return null;
	}

	public SimpleUserBean getSimpleProfileById(String siteUserId) {
		return getSimpleProfileById(siteUserId, false);
	}

	public SimpleUserBean getSimpleProfileById(String siteUserId, boolean isMaster) {
		SimpleUserBean userBean = new SimpleUserBean();
		try {
			userBean = userProfileDao.getSimpleProfileById(siteUserId, isMaster);
		} catch (SQLException e) {
			logger.error("get User Simple Profile by siteUserId error.", e);
		}
		return userBean;
	}

	public SimpleUserBean getSimpleProfileByGlobalUserId(String globalUserId) {
		return getSimpleProfileByGlobalUserId(globalUserId, false);
	}

	public SimpleUserBean getSimpleProfileByGlobalUserId(String globalUserId, boolean isMaster) {
		SimpleUserBean userBean = new SimpleUserBean();
		try {
			userBean = userProfileDao.getSimpleProfileByGlobalUserId(globalUserId, isMaster);
		} catch (SQLException e) {
			logger.error("get User Simple Profile by globalUserId error.", e);
		}
		return userBean;
	}

	public UserFriendBean getFriendProfileById(String siteUserId, String siteFriendId) {
		UserFriendBean bean = null;
		try {
			bean = userProfileDao.getFriendProfileById(siteUserId, siteFriendId);
		} catch (SQLException e) {
			logger.error("get friend Profile error.", e);
		}
		return bean;
	}

	public UserProfileBean getUserProfileById(String siteUserId) {
		UserProfileBean userBean = null;
		try {
			userBean = userProfileDao.getUserProfileById(siteUserId);
		} catch (SQLException e) {
			logger.error("get user profile by userId error.", e);
		}
		return userBean;
	}

	public UserProfileBean getUserProfileByGlobalUserId(String id) {
		UserProfileBean userBean = null;
		try {
			userBean = userProfileDao.getUserProfileByGlobalUserId(id);
			if (userBean != null) {
				userBean.setGlobalUserId(id);
			}
		} catch (SQLException e) {
			logger.error("get user profile by userId error.", e);
		}
		return userBean;
	}

	public boolean updateUserProfile(UserProfileBean userBean) {
		int result = 0;
		try {
			result = userProfileDao.updateProfile(userBean);
		} catch (SQLException e) {
			logger.error("update user profile error.", e);
		}
		return result > 0;
	}

	/**
	 * <pre>
	 * 更新用户状态，状态两种情况
	 * 		1.status=0,站点正常用户状态，新用户默认状态
	 *  		2.status=1,站点禁封状态，无法登陆站点
	 * </pre>
	 *
	 * @param siteUserId
	 * @param status
	 * @return
	 */
	public boolean updateUserStatus(String siteUserId, int status) {
		int result = 0;
		try {
			result = userProfileDao.updateUserStatus(siteUserId, status);
		} catch (SQLException e) {
			logger.error("update user profile error.", e);
		}
		return result > 0;
	}

	public List<SimpleUserRelationBean> getUserRelationPageList(String siteUserId, int pageNum, int pageSize) {
		List<SimpleUserRelationBean> pageList = null;
		try {
			pageList = userProfileDao.getUserRelationPageList(siteUserId, pageNum, pageSize);
		} catch (SQLException e) {
			logger.error("get user page list.", e);
		}
		return pageList;
	}

	public int getTotalUserNum() {
		try {
			return userProfileDao.getTotalUserNum();
		} catch (SQLException e) {
			logger.error("get total user num error.", e);
		}
		return 0;
	}

	public List<SimpleUserBean> getUserPageList(int pageNum, int pageSize) {
		List<SimpleUserBean> pageList = null;
		try {
			pageList = userProfileDao.getUserPageList(pageNum, pageSize);
		} catch (SQLException e) {
			logger.error("get user page list.", e);
		}
		return pageList;
	}

	public boolean getUserMute(String siteUserId) throws SQLException {
		return userProfileDao.isMute(siteUserId);
	}

	public boolean updateUserMute(String siteUserId, boolean mute) {
		try {
			return userProfileDao.updateMute(siteUserId, mute);
		} catch (SQLException e) {
			logger.error("update user mute error.", e);
		}
		return false;
	}
}
