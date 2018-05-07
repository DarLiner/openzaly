package com.akaxin.site.web.admin.service;

import java.util.Map;

public interface IBasicService {

	Map<Integer, String> getSiteConfig();

	boolean updateSiteConfig(String siteUserId, Map<Integer, String> config);

    boolean setUserDefault(String site_user_id);

    boolean delUserDefault(String site_user_id);
}
