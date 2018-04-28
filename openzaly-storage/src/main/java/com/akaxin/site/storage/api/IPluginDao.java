package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.PluginBean;

public interface IPluginDao {

	public boolean addPlugin(PluginBean bean) throws SQLException;

	public boolean updatePlugin(PluginBean bean) throws SQLException;

	public boolean deletePlugin(int pluginId) throws SQLException;

	public PluginBean getPluginProfile(int pluginId) throws SQLException;

	public List<PluginBean> getPluginPageList(int pageNum, int pageSize, int position, int permissionStatus)
			throws SQLException;

	public List<PluginBean> getPluginPageList(int pageNum, int pageSize, int position) throws SQLException;

	public List<PluginBean> getAllPluginList(int pageNum, int pageSize) throws SQLException;

    String reSetAuthKey(int pluginId) throws SQLException;
}
