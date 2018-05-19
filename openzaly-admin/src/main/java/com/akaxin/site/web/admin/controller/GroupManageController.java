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

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.web.admin.exception.UserPermissionException;
import com.akaxin.site.web.admin.service.IBasicService;
import com.akaxin.site.web.admin.service.IGroupService;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private IBasicService basicService;

    // admin.html 为群列表页
    @RequestMapping("/index")
    public ModelAndView toGroupIndex(@RequestBody byte[] bodyParam) {
        ModelAndView modelAndView = new ModelAndView("group/index");
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            List<String> groupDefault = SiteConfigDao.getInstance().getGroupDefault();
            ArrayList<GroupProfileBean> groupProfileBeans = new ArrayList<>();
            modelAndView.addObject("groupDefaultSize", "0");
            if (groupDefault != null && groupDefault.size() > 0) {
                for (String s : groupDefault) {
                    GroupProfileBean groupProfile = groupService.getGroupProfile(s);
                    groupProfileBeans.add(groupProfile);
                }
                modelAndView.addObject("groupList", groupProfileBeans);
                modelAndView.addObject("groupDefaultSize", String.valueOf(groupDefault.size()));
            }
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error("to group index error", e);
        } catch (UserPermissionException e) {
            logger.error("to group index error : "+e.getMessage());
        }
        return new ModelAndView("error");
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

            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String siteGroupId = reqMap.get("group_id");
            GroupProfileBean groupProfile = groupService.getGroupProfile(siteGroupId);
            model.put("group_id", siteGroupId);
            model.put("defaultState", groupProfile.getDefaultState());
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error("to group manage error", e);
        } catch (UserPermissionException e) {
            logger.error("to group manage error : "+e.getMessage());
        }
        return new ModelAndView("error");
    }

    // 跳转到添加群成员界面
    @RequestMapping("/siteUser")
    public ModelAndView toAddMember(@RequestBody byte[] bodyParams) {
        ModelAndView modelAndView = new ModelAndView("group/addMember");
        Map<String, Object> model = modelAndView.getModel();
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String siteGroupId = reqMap.get("group_id");
            model.put("siteGroupId", siteGroupId);
            return modelAndView;
        } catch (InvalidProtocolBufferException  e) {
            logger.error("to group add error", e);
        } catch (UserPermissionException e) {
            logger.error("to group add error : "+e.getMessage());
        }
        return new ModelAndView("error");
    }

    @RequestMapping("/setGroupDefault")
    @ResponseBody
    public String setGroupDefault(@RequestBody byte[] bodyParams) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
            String requestSiteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(requestSiteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String siteGroupId = reqMap.get("group_id");
            boolean flag = groupService.setGroupDefault(siteGroupId);
            if (flag) {
                return SUCCESS;
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("to group add error", e);
        } catch (UserPermissionException e) {
            logger.error("to group add error : "+e.getMessage());
            return NO_PERMISSION;
        }
        return ERROR;
    }

    @RequestMapping("/delGroupDefault")
    @ResponseBody
    public String delGroupDefault(@RequestBody byte[] bodyParams) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String siteGroupId = reqMap.get("group_id");
            boolean flag = groupService.delUserDefault(siteGroupId);
            if (flag) {
                return SUCCESS;
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("del group default error", e);
        } catch (UserPermissionException e) {
            logger.error("del group default error : "+e.getMessage());
            return NO_PERMISSION;
        }
        return ERROR;
    }

    @RequestMapping("toMemberList")
    public ModelAndView toMemberList(@RequestBody byte[] bodyParams) {
        ModelAndView modelAndView = new ModelAndView("group/memberList");
        Map<String, Object> model = modelAndView.getModel();
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
            String siteUserId = getRequestSiteUserId(pluginPackage);
            // 增加权限校验
            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String siteGroupId = reqMap.get("group_id");
            model.put("siteGroupId", siteGroupId);
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error("to group add error", e);
        } catch (UserPermissionException e) {
            logger.error("to group add error : " + e.getMessage());
        }
        return new ModelAndView("error");
    }

    // 跳转群组资料（群信息页面，修改群信息页面）
    @RequestMapping("/profile")
    public ModelAndView toGroupProfile(HttpServletRequest request, @RequestBody byte[] bodyParams) {
        ModelAndView modelAndView = new ModelAndView("group/profile");
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
            String siteUserId = getRequestSiteUserId(pluginPackage);

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
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
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error("to group profile error", e);
        } catch (UserPermissionException e) {
            logger.error("to group profile error : "+e.getMessage());
        }
        return new ModelAndView("error");
    }

    @RequestMapping("/refresh")
    @ResponseBody

    public Map<String, Object> refreshDefault(@RequestBody byte[] bodyParams) {
        HashMap<String, Object> dataMap = new HashMap<>();
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);

        String siteUserId = getRequestSiteUserId(pluginPackage);

        if (!isManager(siteUserId)) {
            throw new UserPermissionException("Current user is not a manager");
        }
        List<String> groupDefault = basicService.getGroupDefault();
        if (groupDefault == null || groupDefault.size() <= 0) {
            dataMap.put("size", 0);
            return dataMap;
        }
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        for (String s : groupDefault) {
            GroupProfileBean bean = groupService.getGroupProfile(s);
            HashMap<String, Object> groupMap = new HashMap<>();
            groupMap.put("siteGroupId", bean.getGroupId());
            groupMap.put("groupName", bean.getGroupName());
            groupMap.put("groupPhoto", bean.getGroupPhoto());
            data.add(groupMap);
        }
            dataMap.put("size", data.size());
            dataMap.put("data", data);
        } catch (InvalidProtocolBufferException e) {
            logger.error("refresh group list error",e);
        } catch (UserPermissionException e) {
            logger.error("refresh group list error : "+e.getMessage());
        }
        return dataMap;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/list")
    @ResponseBody
    public Map<String, Object> getGroupList(HttpServletRequest request, @RequestBody byte[] bodyParams) {
        Map<String, Object> results = new HashMap<String, Object>();
        boolean nodata = true;

        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParams);
            String siteUserId = getRequestSiteUserId(pluginPackage);

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> reqMap = getRequestDataMap(pluginPackage);
                int pageNum = Integer.valueOf(reqMap.get("page"));
                List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                List<SimpleGroupBean> groupList = groupService.getGroupList(pageNum, PAGE_SIZE);
                if (groupList != null && groupList.size() > 0) {
                    List<String> groupDefault = SiteConfigDao.getInstance().getGroupDefault();
                    for (SimpleGroupBean bean : groupList) {
                        if (groupDefault != null && groupDefault.size() > 0) {
                            boolean contains = groupDefault.contains(bean.getGroupId());
                            if (contains) {
                                continue;
                            }
                            Map<String, Object> groupMap = new HashMap<String, Object>();
                            groupMap.put("siteGroupId", bean.getGroupId());
                            groupMap.put("groupName", bean.getGroupName());
                            groupMap.put("groupPhoto", bean.getGroupPhoto());
                            data.add(groupMap);
                        } else {
                            Map<String, Object> groupMap = new HashMap<String, Object>();
                            groupMap.put("siteGroupId", bean.getGroupId());
                            groupMap.put("groupName", bean.getGroupName());
                            groupMap.put("groupPhoto", bean.getGroupPhoto());
                            data.add(groupMap);
                        }
                    }

                }
                results.put("groupData", data);
        } catch (InvalidProtocolBufferException e) {
            logger.error("get group list error", e);
        } catch (UserPermissionException e) {
            logger.error("get group list error : "+e.getMessage());
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

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> reqMap = getRequestDataMap(pluginPackage);
                GroupProfileBean bean = new GroupProfileBean();
                bean.setGroupId(trim(reqMap.get("siteGroupId")));
                bean.setGroupName(trim(reqMap.get("groupName")));
                bean.setGroupPhoto(trim(reqMap.get("groupPhoto")));
                bean.setGroupNotice(trim(reqMap.get("groupNotice")));
                if (groupService.updateGroupProfile(bean)) {
                    return SUCCESS;
                }
        } catch (InvalidProtocolBufferException e) {
            logger.error("update group profile error", e);
        } catch (UserPermissionException e) {
            logger.error("update group profile error : "+e.getMessage());
            return NO_PERMISSION;
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

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
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
        } catch (InvalidProtocolBufferException e) {
            logger.error("get group members error", e);
        } catch (UserPermissionException e) {
            logger.error("get group members error : "+e.getMessage());
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
            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
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
        } catch (InvalidProtocolBufferException e) {
            logger.error("get non group members error", e);
        } catch (UserPermissionException e) {
            logger.error("get non group members error : "+e.getMessage());
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

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, Object> reqMap = getRequestDataMapObj(pluginPackage);
                String siteGroupId = (String) reqMap.get("siteGroupId");
                List<String> memberList = (List<String>) reqMap.get("groupMembers");
                logger.info("siteUserId={} add group={} members={}", siteUserId, siteGroupId, memberList);

                if (groupService.addGroupMembers(siteGroupId, memberList)) {
                    return SUCCESS;
                }
        } catch (InvalidProtocolBufferException e) {
            logger.error("update group profile error", e);
        } catch (UserPermissionException e) {
            logger.error("update group profile error : "+e.getMessage());
            return NO_PERMISSION;
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

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, Object> reqMap = getRequestDataMapObj(pluginPackage);
            String siteGroupId = (String) reqMap.get("siteGroupId");
            List<String> memberList = (List<String>) reqMap.get("groupMembers");
            if (groupService.removeGroupMembers(siteGroupId, memberList)) {
                return SUCCESS;
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("update group profile error", e);
        } catch (UserPermissionException e) {
            logger.error("update group profile error : "+e.getMessage());
            return NO_PERMISSION;
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

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> reqMap = getRequestDataMap(pluginPackage);
                String siteGroupId = reqMap.get("group_id");
                if (groupService.dismissGroup(siteGroupId)) {
                    return SUCCESS;
                }
        } catch (InvalidProtocolBufferException e) {
            logger.error("update group profile error", e);
        } catch (UserPermissionException e) {
            logger.error("update group profile error : "+e.getMessage());
            return NO_PERMISSION;
        }

        return ERROR;
    }
}
