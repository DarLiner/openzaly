package com.akaxin.site.web.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.api.IMessageService;
import com.akaxin.site.message.service.ImMessageService;
import com.akaxin.site.web.admin.bean.WebMessageBean;
import com.akaxin.site.web.admin.service.IMessageManageService;

/**
 * 后台管理发送消息实现
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-25 11:14:05
 */
@Service("messageManageService")
public class MessageManageService implements IMessageManageService {
	private static final Logger logger = LoggerFactory.getLogger(MessageManageService.class);
	private IMessageService imService = new ImMessageService();

	@Override
	public boolean sendU2WebMessage(WebMessageBean bean) {
		CoreProto.U2Web u2Web = CoreProto.U2Web.newBuilder().setMsgId(bean.getMsgId())
				.setSiteUserId(bean.getSiteUserId()).setSiteFriendId(bean.getSiteFriendId())
				.setWebCode(bean.getWebCode()).setHeight(bean.getHeight()).setWidth(bean.getWidth())
				.setTime(System.currentTimeMillis()).build();
		ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
				.setType(MsgType.U2_WEB).setU2Web(u2Web).build();

		Command command = new Command();
		command.setSiteUserId(bean.getSiteUserId());
		command.setSiteFriendId(bean.getSiteFriendId());
		command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
		command.setParams(request.toByteArray());
		boolean result = imService.execute(command);

		logger.info("send u2 web message result={} bean={}", result, bean.toString());
		return result;
	}

	@Override
	public boolean sendU2WebNoticeMessage(WebMessageBean bean) {
		CoreProto.U2WebNotice u2WebNotice = CoreProto.U2WebNotice.newBuilder().setMsgId(bean.getMsgId())
				.setSiteUserId(bean.getSiteUserId()).setSiteFriendId(bean.getSiteFriendId())
				.setWebCode(bean.getWebCode()).setTime(System.currentTimeMillis()).build();
		ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
				.setType(MsgType.U2_WEB_NOTICE).setU2WebNotice(u2WebNotice).build();

		Command command = new Command();
		command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
		command.setSiteUserId(bean.getSiteUserId());
		command.setSiteFriendId(bean.getSiteFriendId());
		command.setParams(request.toByteArray());
		boolean result = imService.execute(command);

		logger.info("send u2 web notice message result={} bean={}", result, bean.toString());
		return result;
	}

	@Override
	public boolean sendGroupWebMessage(WebMessageBean bean) {
		CoreProto.GroupWeb groupWeb = CoreProto.GroupWeb.newBuilder().setMsgId(bean.getMsgId())
				.setSiteUserId(bean.getSiteUserId()).setSiteGroupId(bean.getSiteGroupId()).setWebCode(bean.getWebCode())
				.setHeight(bean.getHeight()).setWidth(bean.getWidth()).setTime(System.currentTimeMillis()).build();
		ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
				.setType(MsgType.GROUP_WEB).setGroupWeb(groupWeb).build();

		Command command = new Command();
		command.setSiteUserId(bean.getSiteUserId());
		command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
		command.setParams(request.toByteArray());
		boolean result = imService.execute(command);

		logger.info("send group web message result={} bean={}", result, bean.toString());
		return result;
	}

	@Override
	public boolean sendGroupWebNoticeMessage(WebMessageBean bean) {
		CoreProto.GroupWebNotice groupWebNotice = CoreProto.GroupWebNotice.newBuilder().setMsgId(bean.getMsgId())
				.setSiteUserId(bean.getSiteUserId()).setSiteGroupId(bean.getSiteGroupId()).setWebCode(bean.getWebCode())
				.setTime(System.currentTimeMillis()).build();
		ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
				.setType(MsgType.GROUP_WEB_NOTICE).setGroupWebNotice(groupWebNotice).build();

		Command command = new Command();
		command.setSiteUserId(bean.getSiteUserId());
		command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
		command.setParams(request.toByteArray());
		boolean result = imService.execute(command);

		logger.info("send group web message result={} bean={}", result, bean.toString());
		return result;
	}

}
