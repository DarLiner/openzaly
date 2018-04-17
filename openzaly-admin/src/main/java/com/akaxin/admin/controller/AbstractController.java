package com.akaxin.admin.controller;

import java.util.Map;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;

public class AbstractController {

	protected final int PAGE_SIZE = 20;

	protected final String SUCCESS = "success";

	protected final String ERROR = "error";

	protected final String NO_PERMISSION = "no-permission";

	public boolean isManager(String siteUserId) {
		return SiteConfig.isSiteManager(siteUserId);
	}

	public boolean isAdmin(String siteUserId) {
		return SiteConfig.isSiteSuperAdmin(siteUserId);
	}

	public String getRequestSiteUserId(PluginProto.ProxyPluginPackage pluginPackage) {
		Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
		return headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getRequestDataMap(PluginProto.ProxyPluginPackage pluginPackage) {
		return GsonUtils.fromJson(pluginPackage.getData(), Map.class);
	}

}
