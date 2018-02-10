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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserDeviceDao;
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.service.DeviceDaoService;

public class UserDeviceDao {
	private static final Logger logger = LoggerFactory.getLogger(UserDeviceDao.class);
	private static UserDeviceDao instance = new UserDeviceDao();

	private IUserDeviceDao userDeviceDaoService = new DeviceDaoService();

	public static UserDeviceDao getInstance() {
		return instance;
	}

	/**
	 * 获取个人设备的具体信息
	 * 
	 * @param siteUserId
	 * @param deviceId
	 * @return
	 */
	public UserDeviceBean getDeviceDetails(String siteUserId, String deviceId) {
		try {
			return userDeviceDaoService.getDeviceDetails(siteUserId, deviceId);
		} catch (SQLException e) {
			logger.error("get device details error", e);
		}
		return null;
	}

	public List<UserDeviceBean> getActiveDeviceList(String siteUserId) {
		try {
			return userDeviceDaoService.getActiveDeviceList(siteUserId);
		} catch (SQLException e) {
			logger.error("get user devices error.", e);
		}
		return null;
	}

	public List<UserDeviceBean> getBoundDevices(String siteUserId) {
		try {
			return userDeviceDaoService.getUserDeviceList(siteUserId);
		} catch (Exception e) {
			logger.error("get user devices error.", e);
		}
		return null;
	}

}
