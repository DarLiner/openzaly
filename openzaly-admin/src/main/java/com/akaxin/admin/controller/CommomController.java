package com.akaxin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("common")
public class CommomController {

	@RequestMapping("success")
	public String toSuccessPage() {
		return "success";
	}

	@RequestMapping("nopermission")
	public String toNoPermissionPage() {
		return "success";
	}

	@RequestMapping("error")
	public String toErrorPage() {
		return "error";
	}

}
