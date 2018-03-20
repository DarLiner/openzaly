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

import com.akaxin.site.storage.api.IPluginDao;
import com.akaxin.site.storage.bean.PluginBean;
import com.akaxin.site.storage.sqlite.SQLitePluginDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:28
 */
public class PluginServiceDao implements IPluginDao {

	@Override
	public boolean addPlugin(PluginBean bean) throws SQLException {
		return SQLitePluginDao.getInstance().addPlugin(bean);
	}

	@Override
	public boolean updatePlugin(PluginBean bean) throws SQLException {
		return SQLitePluginDao.getInstance().updatePlugin(bean);
	}

	@Override
	public boolean deletePlugin(int pluginId) throws SQLException {
		return SQLitePluginDao.getInstance().deletePlugin(pluginId);
	}

	@Override
	public PluginBean getPluginProfile(int pluginId) throws SQLException {
		return SQLitePluginDao.getInstance().queryPluginProfile(pluginId);
	}

	@Override
	public List<PluginBean> getPluginPageList(int pageNum, int pageSize, int position, int permissionStatus)
			throws SQLException {
		return SQLitePluginDao.getInstance().queryPluginList(pageNum, pageSize, position, permissionStatus);
	}

	@Override
	public List<PluginBean> getPluginPageList(int pageNum, int pageSize, int position) throws SQLException {
		return SQLitePluginDao.getInstance().queryPluginList(pageNum, pageSize, position);
	}

	@Override
	public List<PluginBean> getAllPluginList(int pageNum, int pageSize) throws SQLException {
		return SQLitePluginDao.getInstance().queryAllPluginList(pageNum, pageSize);
	}

}
