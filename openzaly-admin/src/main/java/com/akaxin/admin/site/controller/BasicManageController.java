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
package com.akaxin.admin.site.controller;

import static com.akaxin.proto.core.ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.LOG_LEVEL_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.PIC_PATH_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.PIC_SIZE_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.REGISTER_WAY_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_ADDRESS_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_HTTP_ADDRESS_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_HTTP_PORT_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_INTRODUCTION_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_LOGO_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_MANAGER_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_NAME_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.SITE_PORT_VALUE;
import static com.akaxin.proto.core.ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.admin.site.service.IBasicService;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;

@Controller
@RequestMapping("manage")
public class BasicManageController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@Autowired
	private IBasicService basicManageService;

	@RequestMapping("/index")
	public String homePage() {
		return "index";
	}

	// 获取站点配置信息
	@RequestMapping("/basicConfig")
	public ModelAndView toBasic() {
		ModelAndView modelAndView = new ModelAndView("/basic/config");
		Map<String, Object> model = modelAndView.getModel();
		// 设置默认属性
		model.put("site_register_way", "0");
		model.put("pic_size", "1");
		model.put("pic_path", "/akaxin");
		model.put("group_members_count", "100");
		model.put("u2_encryption_status", "1");
		model.put("push_client_status", "0");
		model.put("log_level", "INFO");

		Map<Integer, String> map = basicManageService.getSiteConfig();
		Set<Integer> integers = map.keySet();
		String site_prot = "";
		String site_address = "";
		String http_prot = "";
		String http_address = "";
		for (Integer integer : integers) {
			String res = map.get(integer);
			switch (integer) {
			case SITE_NAME_VALUE:
				model.put("site_name", res);
				break;
			case SITE_ADDRESS_VALUE:
				site_address = res;
				break;
			case SITE_PORT_VALUE:
				site_prot = res;
				break;
			case SITE_HTTP_ADDRESS_VALUE:
				http_address = res;
				break;
			case SITE_HTTP_PORT_VALUE:
				http_prot = res;
				break;
			case SITE_LOGO_VALUE:
				model.put("site_logo", res);
				break;
			case SITE_INTRODUCTION_VALUE:
				model.put("site_desc", res);
				break;
			case REGISTER_WAY_VALUE:
				model.put("site_register_way", res);
				break;
			case PIC_SIZE_VALUE:
				model.put("pic_size", res);
				break;
			case PIC_PATH_VALUE:
				model.put("pic_path", res);
				break;
			case GROUP_MEMBERS_COUNT_VALUE:
				model.put("group_members_count", res);
				break;
			case U2_ENCRYPTION_STATUS_VALUE:
				model.put("u2_encryption_status", res);
				break;
			case PUSH_CLIENT_STATUS_VALUE:
				model.put("push_client_status", res);
				break;
			case LOG_LEVEL_VALUE:
				model.put("log_level", res);
				break;
			case SITE_MANAGER_VALUE:
				model.put("subgenus_admin", res);
				break;
			}

		}
		model.put("siteAddressAndPort", site_address + ":" + site_prot);
		model.put("httpAddressAndPort", http_address + ":" + http_prot);
		return modelAndView;
	}

	// 更新站点配置信息
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/updateConfig")
	@ResponseBody
	public String updateBasicConfig(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			boolean isManager = SiteConfig.isSiteManager(siteUserId);

			if (isManager) {
				Map<String, String> dataMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);
				logger.info("siteUserId={} update config={}", siteUserId, dataMap);
				Map<Integer, String> configMap = new HashMap<Integer, String>();
				configMap.put(ConfigProto.ConfigKey.SITE_NAME_VALUE, dataMap.get("site_name"));
				configMap.put(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE, dataMap.get("site_address"));
				configMap.put(ConfigProto.ConfigKey.SITE_PORT_VALUE, dataMap.get("site_port"));
				configMap.put(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE, dataMap.get("group_members_count"));
				configMap.put(ConfigProto.ConfigKey.PIC_PATH_VALUE, dataMap.get("pic_path"));
				configMap.put(ConfigProto.ConfigKey.SITE_LOGO_VALUE, dataMap.get("site_logo"));
				configMap.put(ConfigProto.ConfigKey.REGISTER_WAY_VALUE, dataMap.get("register_way"));
				configMap.put(ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE, dataMap.get("u2_encryption_status"));
				configMap.put(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE, dataMap.get("push_client_status"));
				configMap.put(ConfigProto.ConfigKey.LOG_LEVEL_VALUE, dataMap.get("log_level"));
				configMap.put(ConfigProto.ConfigKey.SITE_MANAGER_VALUE, dataMap.get("site_manager"));
				if(basicManageService.updateSiteConfig(siteUserId, configMap)) {
					return SUCCESS;
				}
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception e) {
			logger.error("update site config error", e);
		}
		return ERROR;
	}

}
