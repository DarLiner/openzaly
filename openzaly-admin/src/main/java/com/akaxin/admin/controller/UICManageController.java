package com.akaxin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("manageInviteCode")
public class UICManageController {
    @RequestMapping("/index")
    public ModelAndView toManageInviteCode() {
        ModelAndView modelAndView = new ModelAndView("platform/code/index");
        return modelAndView;
    }
}

