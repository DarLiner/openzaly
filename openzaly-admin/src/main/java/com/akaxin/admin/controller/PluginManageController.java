package com.akaxin.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.akaxin.admin.service.IPluginService;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.storage.bean.PluginBean;

//插件扩展管理
@Controller
@RequestMapping("plugin")
public class PluginManageController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

    @Autowired
    private IPluginService pluginService;

    @RequestMapping("/indexPage")
    public ModelAndView toPluginIndex() {
        ModelAndView modelAndView = new ModelAndView("plugin/index");
        return modelAndView;
    }

    @RequestMapping("/addPage")
    public String toPluginAdd() {
        return "plugin/add";
    }

    @RequestMapping("/listPage")
    public String toPluginList() {
        return "plugin/list";
    }

    @RequestMapping("/editPage")
    public ModelAndView toEditPage(@RequestBody byte[] bodyParam) {
        ModelAndView modelAndView = new ModelAndView("plugin/update");
        Map<String, Object> model = modelAndView.getModel();
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (isManager(siteUserId)) {
                //解析Plugin_id
                String data = pluginPackage.getData();
                String[] split = data.split(":\"");
                String res = split[1].replaceAll("\"}", "");
                PluginBean plugin = pluginService.getPlugin(Integer.valueOf(res));
                model.put("name", plugin.getName());
                model.put("url_page", plugin.getUrlPage());
                model.put("api_url", plugin.getApiUrl());
                model.put("plugin_icon", plugin.getIcon());
                model.put("order", plugin.getSort());
                model.put("allow_ip", plugin.getAllowedIp());
                model.put("position", plugin.getPosition());
                model.put("per_status", plugin.getPermissionStatus());
                model.put("id", plugin.getId());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return modelAndView;
    }

    // 增加新扩展
    @RequestMapping(method = RequestMethod.POST, value = "/addPlugin")
    @ResponseBody
    public String addPlugin(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (isManager(siteUserId)) {
                Map<String, String> pluginData = getRequestDataMap(pluginPackage);
                logger.info("siteUserId={} add new plugin={}", siteUserId, pluginData);

                PluginBean bean = new PluginBean();
                bean.setName(pluginData.get("name"));
                bean.setIcon(pluginData.get("plugin_icon"));
                bean.setUrlPage(pluginData.get("url_page"));
                bean.setApiUrl(pluginData.get("api_url"));
                bean.setAllowedIp(pluginData.get("allow_ip"));
                bean.setPosition(Integer.valueOf(pluginData.get("position")));
                bean.setSort(Integer.valueOf(pluginData.get("order")));
                bean.setDisplayMode(PluginProto.PluginDisplayMode.NEW_PAGE_VALUE);
                bean.setPermissionStatus(Integer.valueOf(pluginData.get("per_status")));
                bean.setAddTime(System.currentTimeMillis());
                bean.setAuthKey(StringHelper.generateRandomString(16));// 随机生成

                logger.info("siteUserId={} add new plugin bean={}", siteUserId, bean);
                if (pluginService.addNewPlugin(bean)) {
                    return SUCCESS;
                }

            } else {
                return NO_PERMISSION;
            }
        } catch (Exception e) {
            logger.error("add new plugin controller error", e);
        }
        return ERROR;
    }

    // 获取扩展列表
    @RequestMapping(method = RequestMethod.POST, value = "/getPlugin")
    @ResponseBody
    public Map<String, Object> getPlugin(HttpServletRequest request, @RequestBody byte[] bodyParam) {

        return null;
    }

    // 获取扩展列表
    @RequestMapping(method = RequestMethod.POST, value = "/pluginList")
    @ResponseBody
    public Map<String, Object> getPluginList(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        Map<String, Object> result = new HashMap<String, Object>();
        boolean nodata = true;// 是还有更多数据
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String siteUserId = getRequestSiteUserId(pluginPackage);

            if (isManager(siteUserId)) {
                Map<String, String> dataMap = getRequestDataMap(pluginPackage);
                int pageNum = Integer.valueOf(dataMap.get("page"));
                logger.info("get plugin list ");
                List<PluginBean> pluginList = pluginService.getPluginList(pageNum, PAGE_SIZE);
                List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                if (pluginList != null) {
                    if (PAGE_SIZE == pluginList.size()) {
                        nodata = false;
                    }
                    for (PluginBean bean : pluginList) {
                        Map<String, Object> pluginMap = new HashMap<String, Object>();
                        pluginMap.put("plugin_id", bean.getId());
                        pluginMap.put("name", bean.getName());
                        pluginMap.put("plugin_icon", bean.getIcon());
                        pluginMap.put("url_page", bean.getUrlPage());
                        pluginMap.put("api_url", bean.getApiUrl());
                        pluginMap.put("position", bean.getPosition());
                        pluginMap.put("order", bean.getSort());
                        pluginMap.put("per_status", bean.getPermissionStatus());
                        pluginMap.put("allow_ip", bean.getAllowedIp());
                        // add to list
                        data.add(pluginMap);
                    }
                }

                result.put("pluginData", pluginList);
            }

        } catch (Exception e) {
            logger.error("get plugin list error", e);
        }
        result.put("loading", nodata);
        return result;
    }

    // 编辑扩展
    @RequestMapping(method = RequestMethod.POST, value = "/editPlugin")
    @ResponseBody

    public String editPlugin(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (isManager(siteUserId)) {
                Map<String, String> pluginData = getRequestDataMap(pluginPackage);
                logger.info("siteUserId={} update plugin={}", siteUserId, pluginData);

                PluginBean bean = new PluginBean();
                bean.setId(Integer.valueOf(pluginData.get("plugin_id")));
                bean.setName(pluginData.get("name"));
                bean.setIcon(pluginData.get("plugin_icon"));
                bean.setUrlPage(pluginData.get("url_page"));
                bean.setApiUrl(pluginData.get("api_url"));
                bean.setPosition(Integer.valueOf(pluginData.get("position")));
                bean.setSort(Integer.valueOf(pluginData.get("order")));
                bean.setPermissionStatus(Integer.valueOf(pluginData.get("per_status")));
                bean.setAllowedIp(pluginData.get("allow_ip"));
                logger.info("siteUserId={} update plugin bean={}", siteUserId, bean);

                if (pluginService.updatePlugin(bean)) {
                    return SUCCESS;
                }

            }
        } catch (Exception e) {
            logger.error("edit plugin error", e);
        }
        return ERROR;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delPlugin")
    @ResponseBody
    public String deletePlugin(HttpServletRequest request, @RequestBody byte[] bodyParam) {
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String siteUserId = getRequestSiteUserId(pluginPackage);
            if (isManager(siteUserId)) {
                Map<String, String> dataMap = getRequestDataMap(pluginPackage);
                int pluginId = Integer.valueOf(dataMap.get("plugin_id"));

                if (pluginService.deletePlugin(pluginId)) {
                    return SUCCESS;
                }
            } else {
                return NO_PERMISSION;
            }
        } catch (Exception e) {
            logger.error("edit plugin error", e);
        }
        return ERROR;
    }

}
