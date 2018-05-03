package com.akaxin.site.web.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
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
		modelAndView.addObject("siteName", SiteConfig.getConfig(ConfigProto.ConfigKey.SITE_NAME_VALUE));
		modelAndView.addObject("sessionId", sessionId);
		return modelAndView;
	}

	@RequestMapping(value = "/chatList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<Object> getUserChatList() {
		List<Object> resData = new ArrayList<Object>();
		String siteUserId = "77151873-0fc7-4cf1-8bd6-67d00190fcf6";

		Map<String, String> map = new HashMap<String, String>();
		resData.add(map);
		return resData;
	}

	@RequestMapping(value = "/friendList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getFriendList() {
		List<Object> resData = new ArrayList<Object>();
		String siteUserId = "77151873-0fc7-4cf1-8bd6-67d00190fcf6";

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

	@RequestMapping(value = "/GroupList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<Object> getGroupList() {
		List<Object> resData = new ArrayList<Object>();
		String siteUserId = "77151873-0fc7-4cf1-8bd6-67d00190fcf6";

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
		return resData;
	}
}
