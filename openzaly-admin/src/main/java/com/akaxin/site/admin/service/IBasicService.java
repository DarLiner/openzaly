package com.akaxin.site.admin.service;

import java.util.Map;

public interface IBasicService {

	Map<Integer, String> getSiteConfig();

	boolean updateSiteConfig(String siteUserId, Map<Integer, String> config);
}
