package com.akaxin.site.web.admin.controller;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.storage.bean.MonitorBean;
import com.akaxin.site.web.admin.common.Timeutils;
import com.akaxin.site.web.admin.exception.UserPermissionException;
import com.akaxin.site.web.admin.service.IMonitorService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("monitor")
public class MonitorController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private IMonitorService monitorService;

    @RequestMapping("/index")
    public ModelAndView toMonitor(@RequestBody byte[] bodyParam) {
        ModelAndView modelAndView = new ModelAndView("monitor/index");
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                throw new UserPermissionException("Current user is not a manager");
            }
        Map<String, Object> model = modelAndView.getModel();

        //转换可选时间
        model.put("data_2", Timeutils.getDate(2));
        model.put("data_3", Timeutils.getDate(3));
        model.put("data_4", Timeutils.getDate(4));
        model.put("data_5", Timeutils.getDate(5));
        model.put("data_6", Timeutils.getDate(6));
        model.put("flag", "success");
            return modelAndView;
        } catch (InvalidProtocolBufferException e) {
            logger.error("to data report  error", e);
        } catch (UserPermissionException e) {
            logger.error("to data report  error : "+e.getMessage());
        }
        return new ModelAndView("error");
    }

    @RequestMapping("/refresh")
    @ResponseBody
    public MonitorBean refresh(@RequestBody byte[] bodyParam) {
        PluginProto.ProxyPluginPackage pluginPackage = null;
        int registerNum = 0;
        int messageNum = 0;
        int groupMsgNum = 0;
        int u2MsgNum = 0;
        int userNum = 0;
        int groupNum = 0;
        int friendNum = 0;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                throw new UserPermissionException("Current user is not a manager");
            }

            Map<String, String> uicReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);
            Integer day = null;
            if (uicReqMap != null) {

                day = Integer.parseInt(uicReqMap.get("dayNum"));
            }
            if (day == null) {
                day = 0;
            }

            long now = System.currentTimeMillis();
            registerNum = monitorService.queryNumRegisterPerDay(now, day);
            messageNum = monitorService.queryNumMessagePerDay(now, day);
            groupMsgNum = monitorService.queryGroupMessagePerDay(now, day);
            u2MsgNum = monitorService.queryU2MessagePerDay(now, day);
            userNum = monitorService.getSiteUserNum(now, 0);
            groupNum = monitorService.getGroupNum(now, 0);
            friendNum = monitorService.friendNum(now, 0);
            return new MonitorBean(registerNum, messageNum, groupMsgNum, u2MsgNum, userNum, groupNum, friendNum);

        } catch (InvalidProtocolBufferException e) {
            logger.error("data report refresh error", e);
        } catch (UserPermissionException e) {
            logger.error("data report refresh error : "+e.getMessage());
        }
        return new MonitorBean();
    }
}
