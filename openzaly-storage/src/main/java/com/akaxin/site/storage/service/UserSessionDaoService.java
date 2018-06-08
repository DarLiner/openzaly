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

import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.akaxin.site.storage.bean.UserSessionBean;
import com.akaxin.site.storage.dao.SiteUserSessionDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:08:41
 */
public class UserSessionDaoService implements IUserSessionDao {

	@Override
	public boolean saveUserSession(UserSessionBean bean) throws SQLException {
		return SiteUserSessionDao.getInstance().saveIfAbsent(bean);
	}

	@Override
	public SimpleAuthBean getUserSession(String sessionId) throws SQLException {
		return SiteUserSessionDao.getInstance().queryAuthSession(sessionId);
	}

	@Override
	public List<String> getSessionDeivceIds(String userId) throws SQLException {
		return SiteUserSessionDao.getInstance().queryDeviceIds(userId);
	}

	@Override
	public boolean onlineSession(String siteUserId, String deviceId) throws SQLException {
		return SiteUserSessionDao.getInstance().setOnlineSession(siteUserId, deviceId, true);
	}

	@Override
	public boolean offlineSession(String siteUserId, String deviceId) throws SQLException {
		return SiteUserSessionDao.getInstance().setOnlineSession(siteUserId, deviceId, false);
	}

	@Override
	public boolean deleteUserSession(String siteUserId, String deviceId) throws SQLException {
		return SiteUserSessionDao.getInstance().deleteSession(siteUserId, deviceId);
	}

}
