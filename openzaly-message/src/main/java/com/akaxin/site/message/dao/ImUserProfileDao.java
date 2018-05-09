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
package com.akaxin.site.message.dao;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserDeviceDao;
import com.akaxin.site.storage.api.IUserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.service.DeviceDaoService;
import com.akaxin.site.storage.service.UserProfileDaoService;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-26 18:26:24
 */
public class ImUserProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(ImUserProfileDao.class);
	private IUserProfileDao userProfileDao = new UserProfileDaoService();
	private IUserDeviceDao deviceProfileDao = new DeviceDaoService();

	private ImUserProfileDao() {
	}

	static class SingletonHolder {
		private static ImUserProfileDao instance = new ImUserProfileDao();
	}

	public static ImUserProfileDao getInstance() {
		return SingletonHolder.instance;
	}

	public SimpleUserBean getSimpleUserProfile(String siteUserId) {
		try {
			return userProfileDao.getSimpleProfileById(siteUserId);
		} catch (SQLException e) {
			logger.error("get simple user profile error", e);
		}
		return null;
	}

	public UserProfileBean getUserProfile(String siteUserId) {
		try {
			return userProfileDao.getUserProfileById(siteUserId);
		} catch (SQLException e) {
			logger.error("get simple user profile error", e);
		}
		return null;
	}

	public String getUserToken(String siteUserId) {
		try {
			return deviceProfileDao.getUserToken(siteUserId);
		} catch (SQLException e) {
			logger.error("get user token error.", e);
		}
		return null;
	}

	public String getGlobalUserId(String siteUserId) {
		try {
			return userProfileDao.getGlobalUserId(siteUserId);
		} catch (SQLException e) {
			logger.error("get user token error.", e);
		}
		return null;
	}

	public boolean isMute(String siteUserId) throws SQLException {
		try {
			return userProfileDao.isMute(siteUserId);
		} catch (Exception e) {
			logger.error("get user mute error", e);
		}
		return true;
	}
}
