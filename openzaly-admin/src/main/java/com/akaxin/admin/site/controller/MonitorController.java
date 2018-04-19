package com.akaxin.admin.site.controller;

import com.akaxin.admin.site.service.IMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("monitor")
public class MonitorController {

    @Autowired
    private IMonitorService monitorService;

    @RequestMapping("/index")
    public ModelAndView toMonitor() {
        ModelAndView modelAndView = new ModelAndView("/monitor/index");
        Map<String, Object> model = modelAndView.getModel();
        long now = System.currentTimeMillis();
        int registerNum = monitorService.queryNumRegisterPerDay(now);
        int messageNum = monitorService.queryNumMessagePerDay(now);
        int groupMsgNum = monitorService.queryGroupMessagePerDay(now);
        int u2MsgNum = monitorService.queryU2MessagePerDay(now);
        int userNum = monitorService.getSiteUserNum();
        int groupNum = monitorService.getGroupNum();
        model.put("registerNum", registerNum);
        model.put("groupMsgNum", groupMsgNum);
        model.put("messageNum", messageNum);
        model.put("u2MsgNum", u2MsgNum);
        model.put("userNum", userNum);
        model.put("groupNum", groupNum);

        return null;
    }
}
