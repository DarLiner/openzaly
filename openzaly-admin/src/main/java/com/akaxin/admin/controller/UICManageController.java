package com.akaxin.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.akaxin.admin.service.IUICService;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.UicBean;

//邀请码管理
@Controller
@RequestMapping("uic")
public class UICManageController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);
	private static final int UIC_PAGE_SIZE = 20;

	@Autowired
	private IUICService uicServer;

	@RequestMapping("/index")
	public ModelAndView toUICIndex() {
		ModelAndView modelAndView = new ModelAndView("uic/index");
		return modelAndView;
	}

	@RequestMapping("/unused")
	public String toUnUsed() {
		return "uic/unused_list";
	}
	@RequestMapping(method = RequestMethod.POST, value = "/addUic")
	@ResponseBody
	public String addNewUIC(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			boolean isManager = SiteConfig.isSiteManager(siteUserId);

			if (isManager) {
				return uicServer.addUIC(100) ? SUCCESS : ERROR;
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception e) {
			logger.error("add new uic error", e);
		}
		return ERROR;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/uicList")
	@ResponseBody
	public Map<String, Object> getUICList(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		Map<String, Object> results = new HashMap<String, Object>();
		boolean success = false;
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			boolean isManager = SiteConfig.isSiteManager(siteUserId);
			if (isManager) {
				Map<String, String> uicReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);

				int pageNum = Integer.valueOf(uicReqMap.get("page"));
				int status = Integer.valueOf(uicReqMap.get("code_status"));
				logger.info("-----UIC LIST------pageNum={},status={}", pageNum, status);

				List<UicBean> uicList = uicServer.getUsedUicList(pageNum, UIC_PAGE_SIZE, status);
				List<Map<String, String>> data = new ArrayList<Map<String, String>>();
				if (uicList != null && uicList.size() > 0) {
					if (UIC_PAGE_SIZE == uicList.size()) {
						success = true;
					}
					for (UicBean bean : uicList) {
						Map<String, String> uicMap = new HashMap<String, String>();
						uicMap.put("uic", bean.getUic());
						uicMap.put("siteUserName", bean.getUserName());
						data.add(uicMap);
					}
				}
				results.put("uicData", data);
			}
		} catch (Exception e) {
			logger.error("get used uic list error", e);
		}
		results.put("loading", success);
		return results;
	}

}
