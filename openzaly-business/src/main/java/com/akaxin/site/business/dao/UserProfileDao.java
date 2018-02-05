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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
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

	public SimpleUserBean getSimpleProfileById(String siteUserId) {
		SimpleUserBean userBean = new SimpleUserBean();
		try {
			userBean = userProfileDao.getSimpleProfileById(siteUserId);
		} catch (SQLException e) {
			logger.error("get User Simple Profile error.", e);
		}
		return userBean;
	}

	public SimpleUserBean getSimpleProfileByPubk(String userIdPubk) {
		SimpleUserBean userBean = new SimpleUserBean();
		try {
			userBean = userProfileDao.getSimpleProfileByPubk(userIdPubk);
		} catch (SQLException e) {
			logger.error("get User Simple Profile error.", e);
		}
		return userBean;
	}

	public List<SimpleUserBean> getSimpleProfileByName(String userName) {
		List<SimpleUserBean> userList = new ArrayList<SimpleUserBean>();
		try {
			userList = userProfileDao.getSimpleProfileByName(userName);
		} catch (SQLException e) {
			logger.error("get User Simple Profile error.", e);
		}
		return userList;
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
		} catch (SQLException e) {
			logger.error("get user profile by userId error.", e);
		}
		return userBean;
	}

	public UserProfileBean getUserProfileByPubk(String userIdPubk) {
		UserProfileBean userProfile = new UserProfileBean();
		try {
			userProfile = userProfileDao.getUserProfileByPubk(userIdPubk);
		} catch (SQLException e) {
			logger.error("get user profile by pubk", e);
		}
		return userProfile;
	}

	public boolean updateUserProfile(UserProfileBean userBean) {
		int result = 0;
		try {
			result = userProfileDao.updateUserProfile(userBean);
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

	public List<SimpleUserBean> getUserPageList(int pageNum, int pageSize) {
		List<SimpleUserBean> pageList = null;
		try {
			pageList = userProfileDao.getUserPageList(pageNum, pageSize);
		} catch (SQLException e) {
			logger.error("get user page list.", e);
		}
		return pageList;
	}
}
