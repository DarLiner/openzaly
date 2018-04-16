package com.akaxin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("managePlugin")
public class ManagePlugin {
    @RequestMapping("/index")
    public ModelAndView toManagePluginIndex() {
        ModelAndView modelAndView = new ModelAndView("platform/plugin/index");
        return modelAndView;
    }
}
