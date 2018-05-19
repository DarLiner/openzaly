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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.akaxin.site.web.admin.exception.UserException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.web.admin.service.IUICService;

//邀请码管理
@Controller
@RequestMapping("uic")
public class UICManageController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);
	private static final int UIC_PAGE_SIZE = 20;

	@Autowired
	private IUICService uicServer;

	@RequestMapping("/index")
	public ModelAndView toUICIndex(@RequestBody byte[] bodyParam) {
		ModelAndView modelAndView = new ModelAndView("uic/index");
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			if (!isManager(getRequestSiteUserId(pluginPackage))) {
				throw new UserException("Current user is not a manager");
			}
			return modelAndView;
		} catch (InvalidProtocolBufferException e) {
			logger.error("to Uic error", e);
		} catch (UserException e) {
			logger.error("siteUserId error",e);
		}
		return new ModelAndView("error");
	}

	@RequestMapping("/unused")
	public String toUnUsed(@RequestBody byte[] bodyParam) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			if (!isManager(getRequestSiteUserId(pluginPackage))) {
				throw new UserException("Current user is not a manager");
			}
			return "uic/unused_list";
		} catch (InvalidProtocolBufferException e) {
			logger.error("get unused list error", e);
		} catch (UserException e) {
			logger.error("siteUserId error", e);
		}
		return "error";
	}

	@RequestMapping("/used")
	public String toused(@RequestBody byte[] bodyParam) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			if (!isManager(getRequestSiteUserId(pluginPackage))) {
				throw new UserException("Current user is not a manager");
			}
			return "uic/used_list";
		} catch (InvalidProtocolBufferException e) {
			logger.error("get used list error",e);
		} catch (UserException e) {
			logger.error("siteUserId error",e);
		}
		return "error";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addUic")
	@ResponseBody
	public String addNewUIC(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			boolean isManager = SiteConfig.isSiteManager(siteUserId);
			if (!isManager) {
				throw new UserException("Current user is not a manager");
			}
				return uicServer.addUIC(100, 16) ? SUCCESS : ERROR;
		} catch (InvalidProtocolBufferException e) {
			logger.error("add new uic error", e);
		} catch (UserException e) {
			logger.error("siteUserId error",e);
			return NO_PERMISSION;
		}

		return ERROR;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/uicList")
	@ResponseBody
	public Map<String, Object> getUICList(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		Map<String, Object> results = new HashMap<String, Object>();
		boolean nodata = true;
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			boolean isManager = SiteConfig.isSiteManager(siteUserId);
			if (!isManager) {
				throw new UserException("Current user is not a manager");
			}
				Map<String, String> uicReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);

				int pageNum = Integer.valueOf(uicReqMap.get("page"));
				int status = Integer.valueOf(uicReqMap.get("code_status"));
				logger.info("-----UIC LIST------pageNum={},status={}", pageNum, status);

				List<UicBean> uicList = uicServer.getUsedUicList(pageNum, UIC_PAGE_SIZE, status);
				List<Map<String, String>> data = new ArrayList<Map<String, String>>();
				if (uicList != null && uicList.size() > 0) {
					if (UIC_PAGE_SIZE == uicList.size()) {
						nodata = false;
					}
					for (UicBean bean : uicList) {
						Map<String, String> uicMap = new HashMap<String, String>();
						uicMap.put("uic", bean.getUic());
						uicMap.put("siteUserName", bean.getUserName());
						data.add(uicMap);
					}
				}
				results.put("uicData", data);
		} catch (InvalidProtocolBufferException e) {
			logger.error("get used uic list error", e);
		} catch (UserException e) {
			logger.error("siteUserId error",e);
		}
		results.put("loading", nodata);
		return results;
	}

}
