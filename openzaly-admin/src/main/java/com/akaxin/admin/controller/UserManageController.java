package com.akaxin.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 后台管理-用户管理
 */
@Controller
@RequestMapping("manageUser")
public class UserManageController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

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
