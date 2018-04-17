package com.akaxin.admin.service;

import java.util.List;

import com.akaxin.site.storage.bean.PluginBean;

public interface IPluginService {
	boolean addNewPlugin(PluginBean bean);

	boolean deletePlugin(int pluginId);

	boolean updatePlugin(PluginBean bean);

	PluginBean getPlugin(int pluginId);

	List<PluginBean> getPluginList(int pageNum, int pageSize);
}
