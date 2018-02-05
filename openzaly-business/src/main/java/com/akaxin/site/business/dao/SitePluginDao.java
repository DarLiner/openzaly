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

import com.akaxin.site.storage.api.IPluginDao;
import com.akaxin.site.storage.bean.PluginBean;
import com.akaxin.site.storage.service.PluginServiceDao;

public class SitePluginDao {
	private static final Logger logger = LoggerFactory.getLogger(SitePluginDao.class);
	private IPluginDao pluginDao = new PluginServiceDao();
	private static SitePluginDao instance = new SitePluginDao();

	public static SitePluginDao getInstance() {
		return instance;
	}

	/**
	 * 新增扩展
	 * 
	 * @param bean
	 * @return
	 */
	public boolean addPlugin(PluginBean bean) {
		try {
			return pluginDao.addPlugin(bean);
		} catch (SQLException e) {
			logger.error("add plugin error.", e);
		}
		return false;
	}

	/**
	 * 删除扩展
	 * 
	 * @param pluginId
	 * @return
	 */
	public boolean deletePlugin(int pluginId) {
		try {
			return pluginDao.deletePlugin(pluginId);
		} catch (SQLException e) {
			logger.error("delete plugin error.", e);
		}
		return false;
	}

	/**
	 * <pre>
	 * 禁用扩展，将扩展状态设置为0
	 * 
	 * 扩展的状态
	 * 		1：status=0，扩展禁止使用
	 * 		2：status=1,扩展可用
	 * 		3：status=2,管理员可见
	 * </pre>
	 * 
	 * @param pluginId
	 * @return
	 */
	public boolean updateStatus(int pluginId, int status) {
		try {
			return pluginDao.updatePluginStatus(pluginId, status);
		} catch (SQLException e) {
			logger.error("disable plugin error.", e);
		}
		return false;
	}

	public boolean updatePlugin(PluginBean bean) {
		try {
			return pluginDao.updatePlugin(bean);
		} catch (SQLException e) {
			logger.error("update plugin error.", e);
		}
		return false;
	}

	public List<PluginBean> getPluginPageList(int pageNum, int pageSize, int status) {
		List<PluginBean> pluginList = null;
		try {
			pluginList = pluginDao.getPluginPageList(pageNum, pageSize, status);
		} catch (SQLException e) {
			logger.error("get plugin list error.", e);
		}
		return pluginList;
	}

	/**
	 * 针对管理员用户，兼容两种状态
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param status1
	 * @param status2
	 * @return
	 */
	public List<PluginBean> getPluginPageList(int pageNum, int pageSize, int status1, int status2) {
		List<PluginBean> pluginList = null;
		try {
			pluginList = pluginDao.getPluginPageList(pageNum, pageSize, status1, status2);
		} catch (SQLException e) {
			logger.error("get plugin list error.", e);
		}
		return pluginList;
	}

	public List<PluginBean> getAllPluginList(int pageNum, int pageSize) {
		List<PluginBean> pluginList = null;
		try {
			pluginList = pluginDao.getAllPluginList(pageNum, pageSize);
		} catch (SQLException e) {
			logger.error("get plugin list error.", e);
		}
		return pluginList;
	}

	public PluginBean getPluginProfile(int pluginId) {
		try {
			return pluginDao.getPluginProfile(pluginId);
		} catch (SQLException e) {
			logger.error("get plugin by id error.", e);
		}
		return null;
	}
}
