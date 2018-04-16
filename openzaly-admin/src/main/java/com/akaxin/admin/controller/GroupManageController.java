package com.akaxin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("manageGroup")
public class GroupManageController {
    @RequestMapping("/index")
    public ModelAndView toGroupIndex() {
        ModelAndView modelAndView = new ModelAndView("/platform/group/index");
        return modelAndView;
    }

    @RequestMapping("/groupAdmin")
    public ModelAndView togroupAdmin(String group_id) {
        ModelAndView modelAndView = new ModelAndView("/platform/group/groupAdmin");
        return modelAndView;
    }

    @RequestMapping("/pullList")
    public ModelAndView pullList() {
        ModelAndView modelAndView = new ModelAndView();
        return modelAndView;
    }
}
