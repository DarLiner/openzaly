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
package com.akaxin.site.connector.session;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserDeviceDao;
import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.akaxin.site.storage.service.DeviceDaoService;
import com.akaxin.site.storage.service.UserSessionDaoService;

/**
 * 负责用户session表数据更新
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-04 11:17:13
 */
public class UserSession {
	private static final Logger logger = LoggerFactory.getLogger(UserSession.class);
	private IUserSessionDao userSessionDao = new UserSessionDaoService();
	private IUserDeviceDao userDeviceDao = new DeviceDaoService();

	private UserSession() {

	}

	public static UserSession getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static UserSession instance = new UserSession();
	}

	public SimpleAuthBean getUserSession(String sessionId) {
		SimpleAuthBean bean = new SimpleAuthBean();
		try {
			bean = userSessionDao.getUserSession(sessionId);
		} catch (SQLException e) {
			logger.error("get user session error.", e);
		}
		return bean;
	}

	public boolean updateSessionOnline(String siteUserId, String deviceId) {
		try {
			return userSessionDao.onlineSession(siteUserId, deviceId);
		} catch (SQLException e) {
			logger.error("set user online error.", e);
		}
		return false;
	}

	public boolean updateSessionOffline(String siteUserId, String deviceId) {
		try {
			return userSessionDao.offlineSession(siteUserId, deviceId);
		} catch (SQLException e) {
			logger.error("set user offline error.", e);
		}
		return false;
	}

	public boolean updateActiveTime(String siteUserId, String deviceId) {
		try {
			return userDeviceDao.updateActiveTime(siteUserId, deviceId);
		} catch (SQLException e) {
			logger.error("update active time", e);
		}
		return false;
	}
}
