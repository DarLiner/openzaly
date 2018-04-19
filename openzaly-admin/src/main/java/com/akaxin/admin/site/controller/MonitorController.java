package com.akaxin.admin.site.controller;

import com.akaxin.admin.site.common.Timeutils;
import com.akaxin.admin.site.service.IMonitorService;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("monitor")
public class MonitorController {

    @Autowired
    private IMonitorService monitorService;

    @RequestMapping("/index")
    public ModelAndView toMonitor(@RequestBody byte[] bodyParam) {
        ModelAndView modelAndView = new ModelAndView("/monitor/index");
        Map<String, Object> model = modelAndView.getModel();
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
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
        int registerNum = monitorService.queryNumRegisterPerDay(now, day);
        int messageNum = monitorService.queryNumMessagePerDay(now, day);
        int groupMsgNum = monitorService.queryGroupMessagePerDay(now, day);
        int u2MsgNum = monitorService.queryU2MessagePerDay(now, day);
        int userNum = monitorService.getSiteUserNum(now, day);
        int groupNum = monitorService.getGroupNum(now, day);
        int friendNum = monitorService.friendNum(now, day);
        model.put("registerNum", registerNum);
        model.put("groupMsgNum", groupMsgNum);
        model.put("messageNum", messageNum);
        model.put("u2MsgNum", u2MsgNum);
        model.put("userNum", userNum);
        model.put("groupNum", groupNum);
        model.put("friendNum", friendNum);
        //转换可选时间
        model.put("data_2", Timeutils.getDate(2));
        model.put("data_3", Timeutils.getDate(3));
        model.put("data_4", Timeutils.getDate(4));
        model.put("data_5", Timeutils.getDate(5));
        model.put("data_6", Timeutils.getDate(6));
        model.put("flag", "success");
        return modelAndView;
    }
}
