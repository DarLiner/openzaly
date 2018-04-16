package com.akaxin.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//插件扩展管理
@Controller
@RequestMapping("managePlugin")
public class PluginManageController {
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@RequestMapping("/index")
	public ModelAndView toManagePluginIndex() {
		ModelAndView modelAndView = new ModelAndView("platform/plugin/index");
		return modelAndView;
	}

}
