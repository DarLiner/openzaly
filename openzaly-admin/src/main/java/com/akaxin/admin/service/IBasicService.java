package com.akaxin.admin.service;

import java.util.Map;

public interface IBasicService {
	
	Map<Integer, String> getSiteConfig();

	boolean updateSiteConfig();
}
