package com.akaxin.admin.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.akaxin.admin.service.IBasicService;
import com.akaxin.site.business.impl.site.SiteConfig;

@Service
public class BasicManageService implements IBasicService {

	@Override
	public Map<Integer, String> getSiteConfig() {
		Map<Integer, String> configMap = SiteConfig.getConfigMap();
		return configMap;
	}

	@Override
	public boolean updateSiteConfig() {
		// TODO Auto-generated method stub
		return false;
	}
}
