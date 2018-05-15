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
package com.akaxin.site.web.admin.controller;

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

	public String  trim(String parameter) {
		if (parameter == null) {
			return parameter;
		}
		String trim = parameter.replaceAll("[ 　]", "");
		return trim;
	}
}
