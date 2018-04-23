package com.akaxin.admin.site.controller;

import com.akaxin.admin.site.service.IUserService;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping("site_member")

public class SiteMemberController extends AbstractController {
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
            model.put("site_user_id", siteUserId);
            UserProfileBean userProfile = userService.getUserProfile(siteUserId);
            String userName = userProfile.getUserName();
            model.put("site_user_name", userName);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return new ModelAndView("siteMember/error");
        }
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/pullMemberList")
    @ResponseBody
    public Map<String, Object> getMemberList(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        Map<String, Object> results = new HashMap<String, Object>();
        boolean nodata = true;
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
            String siteUserId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE);

            Map<String, String> ReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);

            int pageNum = Integer.valueOf(ReqMap.get("page"));
            logger.info("-----Member LIST------pageNum={}}", pageNum);
            List<SimpleUserBean> userList = userService.getUserList(pageNum, 10);
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            if (userList != null && userList.size() > 0) {
                if (10 == userList.size()) {
                    nodata = false;
                }
                for (SimpleUserBean bean : userList) {
                    Map<String, String> memberMap = new HashMap<String, String>();
                    memberMap.put("site_user_id", bean.getUserId());
                    memberMap.put("site_user_name", bean.getUserName());
                    data.add(memberMap);
                }
            }
            results.put("Data", data);

        } catch (Exception e) {
            logger.error("get Member list error", e);
        }
        results.put("loading", nodata);
        return results;
    }

}
