package com.akaxin.admin.site.service;

import com.akaxin.admin.site.bean.WebMessageBean;

/**
 * 后台管理发送消息接口
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-25 11:11:10
 */
public interface IMessageManageService {

	boolean sendU2WebMessage(WebMessageBean bean);

	boolean sendU2WebNoticeMessage(WebMessageBean bean);

	boolean sendGroupWebMessage(WebMessageBean bean);

	boolean sendGroupWebNoticeMessage(WebMessageBean bean);
}
