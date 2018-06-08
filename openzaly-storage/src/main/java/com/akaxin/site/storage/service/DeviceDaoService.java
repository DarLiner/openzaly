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
package com.akaxin.site.storage.service;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.api.IUserDeviceDao;
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.dao.SiteUserDeviceDao;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-14 18:03:37
 */
public class DeviceDaoService implements IUserDeviceDao {

	@Override
	public boolean saveUserDevice(UserDeviceBean bean) throws SQLException {
		return SiteUserDeviceDao.getInstance().save(bean);
	}

	@Override
	public boolean updateUserDevice(UserDeviceBean bean) throws SQLException {
		return SiteUserDeviceDao.getInstance().update(bean);
	}

	@Override
	public boolean updateActiveTime(String siteUserId, String deviceId) throws SQLException {
		return SiteUserDeviceDao.getInstance().updateActiveTime(siteUserId, deviceId);
	}

	@Override
	public UserDeviceBean getDeviceDetails(String siteUserId, String deviceId) throws SQLException {
		return SiteUserDeviceDao.getInstance().queryDeviceDetails(siteUserId, deviceId);
	}

	@Override
	public List<UserDeviceBean> getUserDeviceList(String siteUserId) throws SQLException {
		return SiteUserDeviceDao.getInstance().queryDeviceList(siteUserId);
	}

	@Override
	public List<UserDeviceBean> getActiveDeviceList(String siteUserId) throws SQLException {
		return SiteUserDeviceDao.getInstance().queryActiveDeviceList(siteUserId);
	}

	/**
	 * 获取最近的设备
	 */
	@Override
	public UserDeviceBean getLatestDevice(String siteUserId) throws SQLException {
		return SiteUserDeviceDao.getInstance().queryLatestDevice(siteUserId);
	}

	@Override
	public String getDeviceId(String userId, String devicePuk) throws SQLException {
		return SiteUserDeviceDao.getInstance().queryDeviceIdByDevicePuk(userId, devicePuk);
	}

	@Override
	public String getUserToken(String siteUserId) throws SQLException {
		return SiteUserDeviceDao.getInstance().queryUserToken(siteUserId);
	}

	@Override
	public int limitDeviceNum(String siteUserId, int limit) throws SQLException {
		return SiteUserDeviceDao.getInstance().limitDeviceNum(siteUserId, limit);
	}

	@Override
	public boolean delDevice(String siteUserId) throws SQLException {
		return SiteUserDeviceDao.getInstance().delDevice(siteUserId);
	}

}