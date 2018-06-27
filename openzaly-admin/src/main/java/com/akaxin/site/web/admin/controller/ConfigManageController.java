/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.web.admin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.common.logs.AkxLog4jManager;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.ConfigProto.ConfigKey;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.web.admin.exception.UserPermissionException;
import com.akaxin.site.web.admin.service.IConfigService;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 阿卡信 - 后台管理 - 站点设置
 * 
 * akaxin 后台管理配置
 * 
 * @author Sam{@link an.guoyue254@gmail.com} ,Mino
 * @since 2018-05-28 14:10:05
 */
@Controller
@RequestMapping("manage")
public class ConfigManageController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@Autowired
	private IConfigService configManageService;

	@RequestMapping("/index")
	public String homePage(@RequestBody byte[] bodyParam) {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		try {
			pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			String siteUserId = getRequestSiteUserId(pluginPackage);
			boolean isManager = SiteConfig.isSiteManager(siteUserId);
			if (!isManager) {
				throw new UserPermissionException("Current user is not a manager");
			}
			return "admin";
		} catch (InvalidProtocolBufferException e) {
			logger.error("to basic manage error", e);
		} catch (UserPermissionException u) {
			logger.error("to basic manage error : " + u.getMessage());
		}
		return "error";
	}

	// 获取站点配置信息
	@RequestMapping("/basicConfig")
	public ModelAndView toSiteConfigPage(@RequestBody byte[] bodyParam) {

		ModelAndView modelAndView = new ModelAndView("basic/config");
		Map<String, Object> model = modelAndView.getModel();
		// 设置默认属性
		PluginProto.ProxyPluginPackage pluginPackage = null;
		try {
			pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			String siteUserId = getRequestSiteUserId(pluginPackage);
			if (!isManager(siteUserId)) {
				throw new UserPermissionException("Current user is not a manager");
			}
			if (isAdmin(siteUserId)) {
				model.put("manager_type", "admin");
			} else if (isManager(siteUserId)) {
				model.put("manager_type", "site_manager");
			}

			// 设置默认值
			model.put("uic_status", "0");
			model.put("pic_size", "1");
			model.put("pic_path", "/akaxin");
			model.put("group_members_count", "100");
			model.put("u2_encryption_status", "1");
			model.put("push_client_status", "0");
			model.put("log_level", "INFO");
			model.put("add_friends_status", "1");
			model.put("add_groups_status", "1");

			Map<Integer, String> map = configManageService.getSiteConfig();
			Set<Integer> integers = map.keySet();
			String site_prot = "";
			String site_address = "";
			String http_prot = "";
			String http_address = "";
			for (Integer integer : integers) {
				String res = map.get(integer);
				switch (integer) {
				case ConfigKey.SITE_NAME_VALUE:
					model.put("site_name", res);
					break;
				case ConfigKey.SITE_ADDRESS_VALUE:
					site_address = res;
					break;
				case ConfigKey.SITE_PORT_VALUE:
					site_prot = res;
					break;
				case ConfigKey.SITE_HTTP_ADDRESS_VALUE:
					http_address = res;
					break;
				case ConfigKey.SITE_HTTP_PORT_VALUE:
					http_prot = res;
					break;
				case ConfigKey.SITE_LOGO_VALUE:
					model.put("site_logo", res);
					break;
				case ConfigKey.SITE_INTRODUCTION_VALUE:
					model.put("site_desc", res);
					break;
				case ConfigKey.REALNAME_STATUS_VALUE:
					model.put("realName_status", res);
					break;
				case ConfigKey.INVITE_CODE_STATUS_VALUE:
					model.put("uic_status", res);
					break;
				case ConfigKey.PIC_SIZE_VALUE:
					model.put("pic_size", res);
					break;
				case ConfigKey.PIC_PATH_VALUE:
					model.put("pic_path", res);
					break;
				case ConfigKey.GROUP_MEMBERS_COUNT_VALUE:
					model.put("group_members_count", res);
					break;
				case ConfigKey.U2_ENCRYPTION_STATUS_VALUE:
					model.put("u2_encryption_status", res);
					break;
				case ConfigKey.PUSH_CLIENT_STATUS_VALUE:
					model.put("push_client_status", res);
					break;
				case ConfigKey.LOG_LEVEL_VALUE:
					model.put("log_level", res);
					break;
				case ConfigKey.SITE_MANAGER_VALUE:
					model.put("subgenus_admin", res);
					break;
				case ConfigKey.ALLOW_ADD_FRIENDS_VALUE:
					model.put("add_friends_status", res);
					break;
				case ConfigKey.ALLOW_ADD_GROUPS_VALUE:
					model.put("add_groups_status", res);
					break;
				}

			}
			model.put("siteAddressAndPort", site_address + ":" + site_prot);
			model.put("httpAddressAndPort", http_address + ":" + http_prot);
			return modelAndView;
		} catch (InvalidProtocolBufferException e) {
			logger.error("to basic config page error", e);
		} catch (UserPermissionException u) {
			logger.error("to basic config page error : " + u.getMessage());
		}
		return new ModelAndView("error");
	}

	// 更新站点配置信息
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/updateConfig")
	@ResponseBody
	public String updateSiteConfig(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (!isManager(siteUserId)) {
				throw new UserPermissionException("Current user is not a manager");
			}
			Map<String, String> dataMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);
			logger.info("siteUserId={} update config={}", siteUserId, dataMap);
			Map<Integer, String> configMap = new HashMap<Integer, String>();
			if (StringUtils.isNotEmpty(trim(dataMap.get("site_name")))) {
				configMap.put(ConfigProto.ConfigKey.SITE_NAME_VALUE, trim(dataMap.get("site_name")));
			}
			if (StringUtils.isNotEmpty(trim(dataMap.get("site_address")))) {
				configMap.put(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE, trim(dataMap.get("site_address")));
			}
			if (StringUtils.isNotEmpty(trim(dataMap.get("site_port")))) {
				configMap.put(ConfigProto.ConfigKey.SITE_PORT_VALUE, trim(dataMap.get("site_port")));
			}
			if (StringUtils.isNotEmpty(trim(dataMap.get("group_members_count")))) {
				configMap.put(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE,
						trim(dataMap.get("group_members_count")));
			}
			if (StringUtils.isNotEmpty(trim(dataMap.get("pic_path")))) {
				configMap.put(ConfigProto.ConfigKey.PIC_PATH_VALUE, trim(dataMap.get("pic_path")));
			}
			if (StringUtils.isNotEmpty(dataMap.get("site_logo"))) {
				configMap.put(ConfigProto.ConfigKey.SITE_LOGO_VALUE, dataMap.get("site_logo"));
			}
			if (StringUtils.isNotEmpty(dataMap.get("uic_status"))) {
				configMap.put(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE, dataMap.get("uic_status"));
			}
			if (StringUtils.isNotEmpty(dataMap.get("realName_status"))) {
				configMap.put(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE, dataMap.get("realName_status"));
			}
			if (StringUtils.isNotEmpty(dataMap.get("u2_encryption_status"))) {
				configMap.put(ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE, dataMap.get("u2_encryption_status"));
			}

			if (StringUtils.isNotEmpty(dataMap.get("add_friends_status"))) {
				configMap.put(ConfigProto.ConfigKey.ALLOW_ADD_FRIENDS_VALUE, dataMap.get("add_friends_status"));
			}

			if (StringUtils.isNotEmpty(dataMap.get("add_groups_status"))) {
				configMap.put(ConfigProto.ConfigKey.ALLOW_ADD_GROUPS_VALUE, dataMap.get("add_groups_status"));
			}

			if (StringUtils.isNotEmpty(dataMap.get("push_client_status"))) {
				configMap.put(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE, dataMap.get("push_client_status"));
			}
			if (StringUtils.isNotEmpty(dataMap.get("log_level"))) {
				String logLevel = dataMap.get("log_level");
				configMap.put(ConfigProto.ConfigKey.LOG_LEVEL_VALUE, logLevel);
				Level level = Level.INFO;
				if ("DEBUG".equalsIgnoreCase(logLevel)) {
					level = Level.DEBUG;
				} else if ("ERROR".equalsIgnoreCase(logLevel)) {
					level = Level.ERROR;
				}
				// 更新日志级别
				AkxLog4jManager.setLogLevel(level);
			}
			// 普通管理员无权限
			if (isAdmin(siteUserId) && StringUtils.isNotEmpty(trim(dataMap.get("site_manager")))) {
				configMap.put(ConfigProto.ConfigKey.SITE_MANAGER_VALUE, trim(dataMap.get("site_manager")));
			}
			if (configManageService.updateSiteConfig(siteUserId, configMap)) {
				return SUCCESS;
			}
		} catch (InvalidProtocolBufferException e) {
			logger.error("update site config error", e);
		} catch (UserPermissionException u) {
			logger.error("update site config error : " + u.getMessage());
			return NO_PERMISSION;
		}
		return ERROR;
	}

}
