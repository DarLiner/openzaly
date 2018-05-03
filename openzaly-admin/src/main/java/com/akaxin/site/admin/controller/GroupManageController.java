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
package com.akaxin.site.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.akaxin.site.admin.service.IGroupService;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 群组管理控制器
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-17 18:03:07
 */
@Controller
@RequestMapping("group")
public class GroupManageController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@Resource(name = "groupManageService")
	private IGroupService groupService;

	// admin.html 为群列表页
	@RequestMapping("/index")
	public String toGroupIndex() {
		return "group/index";
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getRequestDataMapObj(PluginProto.ProxyPluginPackage pluginPackage) {
		return GsonUtils.fromJson(pluginPackage.getData(), Map.class);
	}

	// 跳转到manage界面
	@RequestMapping("manage")

	public ModelAndView toManage(@RequestBody byte[] bodyParams) {
		ModelAndView modelAndView = new ModelAndView("group/manage");
		Map<String, Object> model = modelAndView.getModel();
		PluginProto.ProxyPluginPackage pluginPackage = null;
		try {
			pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		String siteUserId = getRequestSiteUserId(pluginPackage);
		if (isManager(siteUserId)) {
			Map<String, String> reqMap = getRequestDataMap(pluginPackage);
			String siteGroupId = reqMap.get("group_id");
			model.put("group_id", siteGroupId);
		}
		return modelAndView;
	}

	// 跳转到添加群成员界面
	@RequestMapping("/siteUser")
	public ModelAndView toAddMember(@RequestBody byte[] bodyParams) {
		ModelAndView modelAndView = new ModelAndView("group/addMember");
		Map<String, Object> model = modelAndView.getModel();
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			Map<String, String> reqMap = getRequestDataMap(pluginPackage);
			String siteGroupId = reqMap.get("group_id");
			model.put("siteGroupId", siteGroupId);
		} catch (Exception e) {
			logger.error("to group add error", e);
		}
		return modelAndView;
	}

	@RequestMapping("toMemberList")
	public ModelAndView toMemberList(@RequestBody byte[] bodyParams) {
		ModelAndView modelAndView = new ModelAndView("group/memberList");
		Map<String, Object> model = modelAndView.getModel();
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);
			// 增加权限校验
			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				String siteGroupId = reqMap.get("group_id");
				model.put("siteGroupId", siteGroupId);
			} else {
				new ModelAndView("error");
			}
		} catch (Exception e) {
			logger.error("to group add error", e);
		}
		return modelAndView;
	}

	// 跳转群组资料（群信息页面，修改群信息页面）
	@RequestMapping("/profile")
	public ModelAndView toGroupProfile(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		ModelAndView modelAndView = new ModelAndView("group/profile");
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				String siteGroupId = reqMap.get("group_id");

				GroupProfileBean bean = groupService.getGroupProfile(siteGroupId);
				modelAndView.addObject("siteGroupId", bean.getGroupId());
				modelAndView.addObject("groupName", bean.getGroupName());
				modelAndView.addObject("groupPhoto", bean.getGroupPhoto());
				modelAndView.addObject("ownerUserId", bean.getCreateUserId());
				modelAndView.addObject("groupNotice", bean.getGroupNotice());
				modelAndView.addObject("groupStatus", bean.getGroupStatus());
				modelAndView.addObject("createTime", bean.getCreateTime());
			}

		} catch (Exception e) {
			logger.error("to group profile error", e);
		}

		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/list")
	@ResponseBody
	public Map<String, Object> getGroupList(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		Map<String, Object> results = new HashMap<String, Object>();
		boolean nodata = true;

		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				logger.info("=========page={}", reqMap);
				int pageNum = Integer.valueOf(reqMap.get("page"));
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				List<SimpleGroupBean> groupList = groupService.getGroupList(pageNum, PAGE_SIZE);
				if (groupList != null && groupList.size() > 0) {

					for (SimpleGroupBean bean : groupList) {
						Map<String, Object> groupMap = new HashMap<String, Object>();
						groupMap.put("siteGroupId", bean.getGroupId());
						groupMap.put("groupName", bean.getGroupName());
						groupMap.put("groupPhoto", bean.getGroupPhoto());
						data.add(groupMap);
					}

				}
				results.put("groupData", data);
			}
		} catch (Exception e) {
			logger.error("get group list error", e);
		}
		results.put("loading", nodata);
		return results;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/updateProfile")
	@ResponseBody
	public String updateGroupProfile(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				GroupProfileBean bean = new GroupProfileBean();
				bean.setGroupId(reqMap.get("siteGroupId"));
				bean.setGroupName(reqMap.get("groupName"));
				bean.setGroupPhoto(reqMap.get("groupPhoto"));
				bean.setGroupNotice(reqMap.get("groupNotice"));
				if (groupService.updateGroupProfile(bean)) {
					return SUCCESS;
				}
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception e) {
			logger.error("update group profile error", e);
		}
		return ERROR;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/groupMember")
	@ResponseBody
	public Map<String, Object> getGroupMembers(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		Map<String, Object> results = new HashMap<String, Object>();
		boolean nodata = true;
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				String siteGroupId = reqMap.get("group_id");
				int pageNum = Integer.valueOf(reqMap.get("page"));
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

				List<GroupMemberBean> memberList = groupService.getGroupMembers(siteGroupId, pageNum, PAGE_SIZE);
				if (memberList != null && memberList.size() > 0) {
					if (PAGE_SIZE == memberList.size()) {
						nodata = false;
					}

					for (GroupMemberBean bean : memberList) {
						Map<String, Object> memberMap = new HashMap<String, Object>();
						memberMap.put("siteUserId", bean.getUserId());
						memberMap.put("userName", bean.getUserName());
						memberMap.put("userPhoto", bean.getUserPhoto());
						memberMap.put("userStatus", bean.getUserStatus());
						memberMap.put("userRole", bean.getUserRole());// 是否为群主
						data.add(memberMap);
					}

				}
				results.put("groupMemberData", data);
			}
		} catch (Exception e) {
			logger.error("get group members error", e);
		}
		results.put("loading", nodata);
		return results;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/nonGroupMember")
	@ResponseBody
	public Map<String, Object> getNonGroupMembers(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		Map<String, Object> results = new HashMap<String, Object>();
		boolean nodata = true;
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);
			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				String siteGroupId = reqMap.get("group_id");
				int pageNum = Integer.valueOf(reqMap.get("page"));
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

				List<GroupMemberBean> noMemberList = groupService.getNonGroupMembers(siteGroupId, pageNum, PAGE_SIZE);

				if (noMemberList != null && noMemberList.size() > 0) {
					if (PAGE_SIZE == noMemberList.size()) {
						nodata = false;
					}

					for (GroupMemberBean bean : noMemberList) {
						Map<String, Object> nonMemberMap = new HashMap<String, Object>();
						nonMemberMap.put("siteUserId", bean.getUserId());
						nonMemberMap.put("userName", bean.getUserName());
						nonMemberMap.put("userPhoto", bean.getUserPhoto());
						nonMemberMap.put("userStatus", bean.getUserStatus());
						nonMemberMap.put("userRole", bean.getUserRole());// 这里全部为非群成员
						data.add(nonMemberMap);
					}

				}
				results.put("nonGroupMemberData", data);
			}
		} catch (Exception e) {
			logger.error("get non group members error", e);
		}
		results.put("loading", nodata);
		return results;
	}

	// 添加群组成员：后台添加，群聊不添加通知消息
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/addGroupMember")
	@ResponseBody
	public String addGroupMember(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, Object> reqMap = getRequestDataMapObj(pluginPackage);
				String siteGroupId = (String) reqMap.get("siteGroupId");
				List<String> memberList = (List<String>) reqMap.get("groupMembers");
				logger.info("siteUserId={} add group={} members={}", siteUserId, siteGroupId, memberList);

				if (groupService.addGroupMembers(siteGroupId, memberList)) {
					return SUCCESS;
				}
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception e) {
			logger.error("update group profile error", e);
		}
		return ERROR;
	}

	// 添加群组成员：后台添加，群聊不添加通知消息
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/removeGroupMember")
	@ResponseBody
	public String removeGroupMember(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, Object> reqMap = getRequestDataMapObj(pluginPackage);
				String siteGroupId = (String) reqMap.get("siteGroupId");
				List<String> memberList = (List<String>) reqMap.get("groupMembers");
				logger.info("siteUserId={} remove group={} members={}", siteUserId, siteGroupId, memberList);

				if (groupService.removeGroupMembers(siteGroupId, memberList)) {
					return SUCCESS;
				}
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception e) {
			logger.error("update group profile error", e);
		}
		return ERROR;
	}

	// 解散群聊：逻辑删除
	@RequestMapping(method = RequestMethod.POST, value = "/dissmissGroup")
	@ResponseBody
	public String dissmisGroup(HttpServletRequest request, @RequestBody byte[] bodyParams) {
		try {
			PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
			String siteUserId = getRequestSiteUserId(pluginPackage);

			if (isManager(siteUserId)) {
				Map<String, String> reqMap = getRequestDataMap(pluginPackage);
				String siteGroupId = reqMap.get("group_id");
				logger.info("siteUserId={} dissmis group={}", siteUserId, siteGroupId);

				if (groupService.dismissGroup(siteGroupId)) {
					return SUCCESS;
				}
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception e) {
			logger.error("update group profile error", e);
		}
		return ERROR;
	}
}
