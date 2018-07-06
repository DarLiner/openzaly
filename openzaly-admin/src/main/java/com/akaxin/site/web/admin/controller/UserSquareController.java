package com.akaxin.site.web.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.impl.notice.User2Notice;
import com.akaxin.site.business.push.PushNotification;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.web.admin.service.IUserService;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 用户广场
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-12 18:24:29
 */
@Controller
@RequestMapping("userSquare")
public class UserSquareController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@Resource(name = "userManageService")
	private IUserService userService;

	@RequestMapping("/index")
	public ModelAndView toIndex(@RequestBody byte[] bodyParam) {
		ModelAndView modelAndView = new ModelAndView("siteMember/siteMember");
		Map<String, Object> model = modelAndView.getModel();
		PluginProto.ProxyPluginPackage pluginPackage = null;
		try {
			pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			UserProfileBean userProfile = userService.getUserProfile(siteUserId);
			model.put("site_user_id", siteUserId);
			model.put("site_user_name", userProfile.getUserName());
		} catch (InvalidProtocolBufferException e) {
			logger.error("to user square error", e);
			return new ModelAndView("siteMember/error");
		}

		return modelAndView;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/pullMemberList")
	@ResponseBody
	public Map<String, Object> getMemberList(HttpServletRequest request, @RequestBody byte[] bodyParam) {
		Map<String, Object> results = new HashMap<String, Object>();
		boolean nodata = true;
		int pageSize = 20;
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);

			Map<String, String> ReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);

			int pageNum = Integer.valueOf(ReqMap.get("pageNum"));
			List<SimpleUserBean> userList = userService.getUserList(pageNum, pageSize);
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			if (userList != null && userList.size() > 0) {
				for (SimpleUserBean bean : userList) {
					Map<String, String> memberMap = new HashMap<String, String>();
					if (siteUserId != bean.getUserId()) {
						memberMap.put("site_user_id", bean.getUserId());
						memberMap.put("site_user_name", bean.getUserName());
						memberMap.put("site_user_photo", bean.getUserPhoto());
						UserProto.UserRelation userRelation = UserFriendDao.getInstance().getUserRelation(siteUserId,
								bean.getUserId());
						memberMap.put("site_user_relation", String.valueOf(userRelation.getNumber()));
					}
					data.add(memberMap);
				}
				nodata = false;
			} 
			results.put("Data", data);

		} catch (Exception e) {
			logger.error("get Member list error", e);
		}
		results.put("loading", nodata);
		return results;
	}

	@RequestMapping("/applyFriend")
	@ResponseBody
	public String[] applyFriend(@RequestBody byte[] bodyParam) {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		try {
			pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
			String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);
			Map<String, String> ReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);
			String friendSiteUserId = ReqMap.get("site_user_id");
			String apply_reason = ReqMap.get("apply_reason");
			if (StringUtils.isBlank(siteUserId)) {
				return new String[] { "添加失败", "0" };
			} else if (siteUserId.equals(friendSiteUserId)) {

			} else {
				int applyTimes = UserFriendDao.getInstance().getApplyCount(friendSiteUserId, siteUserId);
				if (applyTimes >= 5) {
					return new String[] { "失败,次数过多", "0" };

				} else {
					if (UserFriendDao.getInstance().saveFriendApply(siteUserId, friendSiteUserId, apply_reason)) {
						new User2Notice().applyFriendNotice(siteUserId, friendSiteUserId);
						// 同时下发一条PUSH消息
						if (applyTimes < 2) {
							PushNotification.sendAddFriend(siteUserId, friendSiteUserId);
						}
						return new String[] { "成功", friendSiteUserId };
					}
				}
			}
		} catch (InvalidProtocolBufferException e) {
			logger.error("Friend apply error.", e);
		}
		return new String[] { "添加失败", "0" };

	}

}
