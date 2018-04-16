package com.akaxin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("manageUser")
public class ManageUserController {
    @RequestMapping("/index")
    public ModelAndView toUserIndex() {
        ModelAndView modelAndView = new ModelAndView("platform/user/index");
        return modelAndView;
    }

    @RequestMapping("/getSiteUserInfo")
    public ModelAndView getSiteUserInfo(String id) {
        ModelAndView modelAndView = new ModelAndView();
        return modelAndView;
    }

    @RequestMapping("/pullSiteUsers")
    public ModelAndView pullSiteUsers() {
        ModelAndView modelAndView = new ModelAndView();
        return modelAndView;
    }

    @RequestMapping("/sealupSiteUser")
    public boolean sealupSiteUser(String id) {
        return true;
    }

}
