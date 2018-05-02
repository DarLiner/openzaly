package com.akaxin.site.web.controller;

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
@RequestMapping("webChat")
public class WebChatController {

	@RequestMapping("/main")
	public ModelAndView toMainIndex() {
		return new ModelAndView("webChat/akaxin_chat_index");
	}

}
