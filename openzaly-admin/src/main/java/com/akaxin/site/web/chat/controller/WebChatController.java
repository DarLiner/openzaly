package com.akaxin.site.web.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.business.cache.WebSessionCache;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.web.chat.service.WebChatService;

/**
 * WebChat聊天控制器，提供WEB聊天网页版本
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-02 11:46:51
 */
@Controller
@RequestMapping("akaxin")
public class WebChatController {

	@Resource(name = "webChatService")
	private WebChatService webChatService;

	// 跳转到扫描二维码界面
	@RequestMapping("/index")
	public ModelAndView toChatIndex() {
		ModelAndView modelAndView = new ModelAndView("webChat/akaxin_chat_index");
		modelAndView.getModel().put("siteName", SiteConfig.getConfig(ConfigProto.ConfigKey.SITE_NAME_VALUE));
		modelAndView.getModel().put("siteAddress", SiteConfig.getSiteAddress());
		return modelAndView;
	}

	// 跳转到聊天主页面
	@RequestMapping("/chat")
	public ModelAndView toChatMain(@RequestParam String sessionId) {
		ModelAndView modelAndView = new ModelAndView("webChat/akaxin_chat_main");
		System.out.println("/akaxin/chat sessionid=" + sessionId);
		String siteUserId = getSiteUserId(sessionId);

		if (StringUtils.isNotEmpty(siteUserId)) {
			UserProfileBean bean = webChatService.getUserProfile(siteUserId);
			if (bean != null) {
				modelAndView.addObject("siteLogoId", SiteConfig.getSiteLogo());
				modelAndView.addObject("siteName", SiteConfig.getConfig(ConfigProto.ConfigKey.SITE_NAME_VALUE));
				modelAndView.addObject("sessionId", sessionId);
				modelAndView.addObject("userId", bean.getSiteUserId());
				modelAndView.addObject("userName", bean.getUserName());
				modelAndView.addObject("userPhoto", bean.getUserPhoto());
			}
		}

		return modelAndView;
	}

	@RequestMapping(value = "/chatList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getChatSessions(@RequestParam String sessionId) {
		List<Object> resData = new ArrayList<Object>();
		String siteUserId = getSiteUserId(sessionId);

		if (StringUtils.isEmpty(siteUserId)) {
			return null;
		}

		List<SimpleUserBean> friendList = webChatService.getUserFriendList(siteUserId);
		if (friendList != null) {
			for (SimpleUserBean bean : friendList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", bean.getUserId());
				map.put("stype", "u2");// u2 or group
				map.put("name", bean.getUserName());
				map.put("photo", bean.getUserPhoto());
				map.put("msg", "目前我们已经解决了大部分问题");
				resData.add(map);
			}
		}

		return GsonUtils.toJson(resData);
	}

	@RequestMapping(value = "/friendList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getFriendList(@RequestParam String sessionId) {
		List<Object> resData = new ArrayList<Object>();
		String siteUserId = getSiteUserId(sessionId);

		List<SimpleUserBean> friendList = webChatService.getUserFriendList(siteUserId);
		if (friendList != null) {
			for (SimpleUserBean bean : friendList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("siteUserId", bean.getUserId());
				map.put("userName", bean.getUserName());
				map.put("userPhoto", bean.getUserPhoto());
				resData.add(map);
			}
		}
		return GsonUtils.toJson(resData);
	}

	@RequestMapping(value = "/groupList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getGroupList(@RequestParam String sessionId) {
		List<Object> resData = new ArrayList<Object>();
		String siteUserId = getSiteUserId(sessionId);

		if (StringUtils.isEmpty(siteUserId)) {
			return GsonUtils.toJson(resData);
		}

		List<SimpleGroupBean> groupList = webChatService.getUserGroupList(siteUserId);
		if (groupList != null) {
			for (SimpleGroupBean bean : groupList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("siteGroupId", bean.getGroupId());
				map.put("groupName", bean.getGroupName());
				map.put("groupPhoto", bean.getGroupPhoto());
				resData.add(map);
			}
		}

		return GsonUtils.toJson(resData);
	}

	private String getSiteUserId(String sessionId) {
		if (StringUtils.isEmpty(sessionId)) {
			return null;
		}
		WebSessionCache.getSiteUserId(sessionId);
		return "77151873-0fc7-4cf1-8bd6-67d00190fcf6";
	}

}
