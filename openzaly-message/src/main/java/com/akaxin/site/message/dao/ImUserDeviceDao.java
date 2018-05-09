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
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.service.DeviceDaoService;

/**
 * 获取手机设备相关
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-09 18:41:49
 */
public class ImUserDeviceDao {
	private static final Logger logger = LoggerFactory.getLogger(ImUserDeviceDao.class);
	private static ImUserDeviceDao instance = new ImUserDeviceDao();

	private IUserDeviceDao userDeviceDaoService = new DeviceDaoService();

	public static ImUserDeviceDao getInstance() {
		return instance;
	}

	/**
	 * 获取个人设备公钥
	 * 
	 * @param siteUserId
	 * @param deviceId
	 * @return
	 */
	public String getDevicePubk(String siteUserId, String deviceId) {
		try {
			return userDeviceDaoService.getDevicePubk(siteUserId, deviceId);
		} catch (SQLException e) {
			logger.error("get device pubk error", e);
		}
		return null;
	}

}
