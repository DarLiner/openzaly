package com.akaxin.site.web.chat.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.business.impl.site.SiteConfig;

/**
 * 获取站点配置信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-02 11:46:51
 */
@Controller
@RequestMapping("site")
public class SiteConfigController {

	@RequestMapping(value = "/config", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getSiteName() {
		Map<String, Object> siteMap = new HashMap<String, Object>();
		siteMap.put("siteName", SiteConfig.getConfig(ConfigProto.ConfigKey.SITE_NAME_VALUE));
		siteMap.put("siteAddress", SiteConfig.getSiteAddress());
		return GsonUtils.toJson(siteMap);
	}

	@RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> webLogin(@RequestParam String loginKey) {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("/login " + loginKey);
		// 默认验证成功
		String webSessionId = UUID.randomUUID().toString();
		map.put("sessionId", webSessionId);
		map.put("success", true);
		return map;
	}

}
