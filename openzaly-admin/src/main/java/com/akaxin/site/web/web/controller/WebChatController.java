package com.akaxin.site.web.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * WebChat聊天控制器，提供WEB聊天网页版本
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-02 11:46:51
 */
@Controller
@RequestMapping("akaxin")
public class WebChatController {

	// 跳转到扫描二维码界面
	@RequestMapping("/index")
	public ModelAndView toChatIndex() {
		return new ModelAndView("webChat/akaxin_chat_index");
	}

	// 跳转到聊天主页面
	@RequestMapping("/chat")
	public ModelAndView toChatMain() {
		return new ModelAndView("webChat/akaxin_chat_main");
	}

}
