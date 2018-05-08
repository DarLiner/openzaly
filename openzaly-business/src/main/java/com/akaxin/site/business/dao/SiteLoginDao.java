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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserDeviceDao;
import com.akaxin.site.storage.api.IUserProfileDao;
import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.bean.UserSessionBean;
import com.akaxin.site.storage.service.DeviceDaoService;
import com.akaxin.site.storage.service.UserProfileDaoService;
import com.akaxin.site.storage.service.UserSessionDaoService;

import javax.transaction.TransactionRequiredException;

/**
 * 用户登陆使用dao，负责用户信息入库
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.20
 */
public class SiteLoginDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteLoginDao.class);
	private static SiteLoginDao userLoginDao = new SiteLoginDao();

	private IUserDeviceDao userDeviceDao = new DeviceDaoService();
	private IUserProfileDao userProfileDao = new UserProfileDaoService();
	private IUserSessionDao userSessionDao = new UserSessionDaoService();

	public static SiteLoginDao getInstance() {
		return userLoginDao;
	}

	public boolean registerUser(UserProfileBean userBean) {
		try {
			return userProfileDao.saveUserProfile(userBean);
		} catch (SQLException e) {
			logger.error("register user error.", e);
		}
		return false;
	}

	public boolean saveUserDevice(UserDeviceBean deviceBean) {
		try {
			return userDeviceDao.saveUserDevice(deviceBean);
		} catch (SQLException e) {
			logger.error("save user Device error.", e);
		}
		return false;
	}

	public boolean updateUserDevice(UserDeviceBean deviceBean) {
		try {
			return userDeviceDao.updateUserDevice(deviceBean);
		} catch (SQLException e) {
			logger.error("update user Device error.", e);
		}
		return false;
	}

	public boolean saveUserSession(UserSessionBean sessionBean) {
		try {
			return userSessionDao.saveUserSession(sessionBean);
		} catch (SQLException e) {
			logger.error("save user session error.", e);
		}
		return false;
	}

	public String checkDeviceId(String userId, String userDevicePuk) {
		try {
			return userDeviceDao.getDeviceId(userId, userDevicePuk);
		} catch (SQLException e) {
			logger.error("check device id error,", e);
		}
		return null;
	}

	public String getSiteUserId(String userIdPubk) {
		try {
			return userProfileDao.getSiteUserId(userIdPubk);
		} catch (SQLException e) {
			logger.error("get site user id error.", e);
		}
		return null;
	}

	public boolean deleteSession(String siteUserId, String deviceId) {
		try {
			return userSessionDao.deleteUserSession(siteUserId, deviceId);
		} catch (SQLException e) {
			logger.error("delete session error.", e);
		}
		return false;
	}

	public boolean addDefault(UserProfileBean regBean) {
		return true;
	}
}
