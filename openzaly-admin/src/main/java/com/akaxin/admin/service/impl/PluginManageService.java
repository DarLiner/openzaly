package com.akaxin.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akaxin.admin.service.IPluginService;
import com.akaxin.site.business.dao.SitePluginDao;
import com.akaxin.site.storage.bean.PluginBean;

/**
 * 扩展管理Service
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-17 11:57:26
 */
@Service
public class PluginManageService implements IPluginService {

	@Override
	public boolean addNewPlugin(PluginBean bean) {
		return SitePluginDao.getInstance().addPlugin(bean);
	}

	@Override
	public boolean deletePlugin(int pluginId) {
		return SitePluginDao.getInstance().deletePlugin(pluginId);
	}

	@Override
	public boolean updatePlugin(PluginBean bean) {
		return SitePluginDao.getInstance().updatePlugin(bean);
	}

	@Override
	public PluginBean getPlugin(int pluginId) {
		return SitePluginDao.getInstance().getPluginProfile(pluginId);
	}

	@Override
	public List<PluginBean> getPluginList(int pageNum, int pageSize) {
		return SitePluginDao.getInstance().getAllPluginList(pageNum, pageSize);
	}

}
