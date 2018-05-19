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

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.web.admin.exception.UserPermissionException;
import com.akaxin.site.web.admin.service.IBasicService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.core.UserProto.UserStatus;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.web.admin.service.IUserService;

/**
 * 后台管理-用户管理
 */
@Controller
@RequestMapping("user")
public class UserManageController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

    @Resource(name = "userManageService")
    private IUserService userService;
    @Autowired
    private IBasicService basicService;

    // admin.html 分页获取用户列表
    @RequestMapping("/index")
    public ModelAndView toUserIndex(@RequestBody byte[] bodyParam) {
        ModelAndView modelAndView = new ModelAndView("user/index");
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                throw new UserPermissionException("Current user is not a manager");
            }
        List<String> userDefault = SiteConfigDao.getInstance().getUserDefault();
        List<UserProfileBean> userProfileBeans = new ArrayList<>();
        modelAndView.addObject("userDefaultSize", "0");
        if (userDefault != null && userDefault.size() > 0) {
            for (String siteUserId : userDefault) {
                UserProfileBean userProfile = userService.getUserProfile(siteUserId);
                userProfileBeans.add(userProfile);
            }
            modelAndView.addObject("userList", userProfileBeans);
            modelAndView.addObject("userDefaultSize", String.valueOf(userDefault.size()));
        }
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error("to User Manage error", e);
        } catch (UserPermissionException e) {
            logger.error("to User Manage error : "+e.getMessage());
        }
        return new ModelAndView("error");
    }

    @RequestMapping("/setUserDefault")
    @ResponseBody
    public String setUserDefault(@RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String site_user_id = reqMap.get("siteUserId");
            boolean flag = basicService.setUserDefault(site_user_id);
            if (flag) {
                return SUCCESS;
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("to set User Default  error", e);
        } catch (UserPermissionException e) {
            logger.error("to set User Default  error : "+e.getMessage());
            return NO_PERMISSION;
        }
        return ERROR;
    }

    @RequestMapping("/delUserDefault")
    @ResponseBody
    public String delUserDefault(@RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                throw new UserPermissionException("Current user is not a manager");
            }
            Map<String, String> reqMap = getRequestDataMap(pluginPackage);
            String site_user_id = reqMap.get("siteUserId");
            boolean flag = basicService.delUserDefault(site_user_id);
            if (flag) {
                return SUCCESS;
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("to del User Default  error", e);
        } catch (UserPermissionException e) {
            logger.error("to del User Default  error : "+e.getMessage());
            return NO_PERMISSION;
        }
        return ERROR;
    }

    // 用户个人资料展示界面，此界面编辑用户资料，并执行更新
    @RequestMapping("/profile")
    public ModelAndView toUserProfile(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        ModelAndView modelAndView = new ModelAndView("user/profile");

        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String currentUserId = getRequestSiteUserId(pluginPackage);

            if (!isManager(currentUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> reqMap = getRequestDataMap(pluginPackage);
                String siteUserId = reqMap.get("site_user_id");
                UserProfileBean bean = userService.getUserProfile(siteUserId);
                modelAndView.addObject("siteUserId", bean.getSiteUserId());
                modelAndView.addObject("userName", bean.getUserName());
                modelAndView.addObject("userPhoto", bean.getUserPhoto());
                modelAndView.addObject("userIntroduce", bean.getSelfIntroduce());
                modelAndView.addObject("userStatus", bean.getUserStatus());
                modelAndView.addObject("regTime", bean.getRegisterTime());
                modelAndView.addObject("defaultState", bean.getDefaultState());
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error(StringHelper.format("siteUserId={} get user profile error"), e);
        } catch (UserPermissionException e) {
            logger.error("get user profile error : "+e.getMessage());
        }
        return new ModelAndView("error");
    }

    @RequestMapping("/refresh")
    @ResponseBody
    public Map<String, Object> refreshDefault(@RequestBody byte[] bodyParam) {
        HashMap<String, Object> dataMap = new HashMap<>();

        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                throw new UserPermissionException("Current user is not a manager");
            }
        List<String> userDefault = basicService.getUserDefault();
        if (userDefault == null || userDefault.size() <= 0) {
            dataMap.put("size", 0);
            return dataMap;
        }
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        for (String s : userDefault) {
            UserProfileBean bean = userService.getUserProfile(s);
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("siteUserId", bean.getSiteUserId());
            userMap.put("userName", bean.getUserName());
            userMap.put("userPhoto", bean.getUserPhoto());
            userMap.put("userStatus", bean.getUserStatus());
            data.add(userMap);
        }
            dataMap.put("size", data.size());
            dataMap.put("data", data);
        } catch (InvalidProtocolBufferException e) {
            logger.error("refresh user list error",e);
        } catch (UserPermissionException e) {
            logger.error("refresh user list error : "+e.getMessage());
        }

        return dataMap;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/userList")
    @ResponseBody
    public Map<String, Object> getSiteUsers(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        Map<String, Object> results = new HashMap<String, Object>();
        boolean nodata = true;

        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String siteUserId = getRequestSiteUserId(pluginPackage);

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> dataMap = getRequestDataMap(pluginPackage);
                int pageNum = Integer.valueOf(dataMap.get("page"));

                List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                List<SimpleUserBean> userList = userService.getUserList(pageNum, PAGE_SIZE);
                if (userList != null && userList.size() > 0) {
                    if (PAGE_SIZE == userList.size()) {
                        nodata = false;
                    }
                    List<String> userDefault = SiteConfigDao.getInstance().getUserDefault();
                    if (userDefault != null && userDefault.size() > 0) {
                        for (SimpleUserBean bean : userList) {
                            boolean contains = userDefault.contains(bean.getUserId());
                            Map<String, Object> userMap = new HashMap<String, Object>();
                            if (contains) {
                                continue;
                            }
                            userMap.put("siteUserId", bean.getUserId());
                            userMap.put("userName", bean.getUserName());
                            userMap.put("userPhoto", bean.getUserPhoto());
                            userMap.put("userStatus", bean.getUserStatus());

                            data.add(userMap);
                        }
                    } else {
                        for (SimpleUserBean bean : userList) {
                            Map<String, Object> userMap = new HashMap<String, Object>();
                            userMap.put("siteUserId", bean.getUserId());
                            userMap.put("userName", bean.getUserName());
                            userMap.put("userPhoto", bean.getUserPhoto());
                            userMap.put("userStatus", bean.getUserStatus());

                            data.add(userMap);
                        }
                    }

                }

                results.put("userData", data);

        } catch (InvalidProtocolBufferException e) {
            logger.error("get site user list error", e);
        } catch (UserPermissionException e) {
            logger.error("get site user list error : "+e.getMessage());
        }
        results.put("loading", nodata);
        return results;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateProfile")
    @ResponseBody
    public String updateProfile(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String siteUserId = getRequestSiteUserId(pluginPackage);

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> reqMap = getRequestDataMap(pluginPackage);
                UserProfileBean bean = new UserProfileBean();
                bean.setSiteUserId(trim(reqMap.get("siteUserId")));
                bean.setUserName(trim(reqMap.get("userName")));
                bean.setUserPhoto(trim(reqMap.get("userPhoto")));
                bean.setSelfIntroduce(trim(reqMap.get("userIntroduce")));
                if (userService.updateProfile(bean)) {
                    return SUCCESS;
                }

        } catch (InvalidProtocolBufferException e) {
            logger.error("update profile error", e);
        } catch (UserPermissionException e) {
            logger.error("update profile error : "+e.getMessage());
            return NO_PERMISSION;
        }

        return ERROR;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/sealup")
    @ResponseBody
    public String sealup(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String siteUserId = getRequestSiteUserId(pluginPackage);

            if (!isManager(siteUserId)) {
                throw new UserPermissionException("Current user is not a manager");
            }
                Map<String, String> reqMap = getRequestDataMap(pluginPackage);
                String reqStatus = reqMap.get("type");
                int status = UserStatus.NORMAL_VALUE;
                if ("1".equals(reqStatus)) {
                    status = UserStatus.SEALUP_VALUE;
                }

                if (userService.sealUpUser(reqMap.get("site_user_id"), status)) {
                    return SUCCESS;
                }
        } catch (InvalidProtocolBufferException e) {
            logger.error("update profile error", e);
        } catch (UserPermissionException e) {
            logger.error("update profile error : "+e.getMessage());
            return NO_PERMISSION;
        }
        return ERROR;
    }

}
