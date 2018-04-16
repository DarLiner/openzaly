package com.akaxin.admin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.akaxin.admin.service.IBasicService;
import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.business.impl.site.SiteConfig;

@Service
public class BasicManageService implements IBasicService {
	private static final Logger logger = LoggerFactory.getLogger(BasicManageService.class);

	@Override
	public Map<Integer, String> getSiteConfig() {
		Map<Integer, String> configMap = SiteConfig.getConfigMap();
		return configMap;
	}

	@Override
	public boolean updateSiteConfig(String siteUserId, Map<Integer, String> config) {
		boolean isAdmin = SiteConfig.isSiteSuperAdmin(siteUserId);
		boolean result = SiteConfigDao.getInstance().updateSiteConfig(config, isAdmin);
		SiteConfig.updateConfig();
		logger.info("siteUserId={} isAdmin={} update configMap={} result={}", siteUserId, isAdmin, config, result);
		return result;
	}

}
