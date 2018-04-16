package com.akaxin.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//邀请码管理
@Controller
@RequestMapping("manageInviteCode")
public class UICManageController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@RequestMapping("/index")
	public ModelAndView toManageInviteCode() {
		ModelAndView modelAndView = new ModelAndView("platform/code/index");
		return modelAndView;
	}

}
