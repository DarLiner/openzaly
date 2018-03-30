/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.site.business.impl.notice;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.client.ImStcNoticeProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.business.constant.NoticeText;
import com.akaxin.site.business.dao.UserSessionDao;
import com.akaxin.site.message.api.IMessageService;
import com.akaxin.site.message.service.ImMessageService;
import com.google.protobuf.ByteString;

public class User2Notice {
	private static final Logger logger = LoggerFactory.getLogger(User2Notice.class);
	private IMessageService u2MsgService = new ImMessageService();

	/**
	 * A用户添加用户B为好友，发送B有好友添加的申请 <br>
	 * 
	 * 客户端接收到此通知，会在通讯录的好友申请提示
	 * 
	 * @param siteUserId
	 *            用户B的用户ID
	 */
	public void applyFriendNotice(String siteUserId) {
		List<String> deviceList = UserSessionDao.getInstance().getSessionDevices(siteUserId);
		for (String deviceId : deviceList) {
			CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
					.setAction(CommandConst.IM_NOTICE);
			ImStcNoticeProto.ImStcNoticeRequest noticeRequest = ImStcNoticeProto.ImStcNoticeRequest.newBuilder()
					.setType(ImStcNoticeProto.NoticeType.APPLY_FRIEND).build();
			commandResponse.setParams(noticeRequest.toByteArray());
			commandResponse.setErrCode(ErrorCode.SUCCESS);
			ChannelWriter.writeByDeviceId(deviceId, commandResponse);
			logger.debug("apply friend notice. to siteUserId={} deviceId={}", siteUserId, deviceId);
		}
	}

	/**
	 * <pre>
	 * 代发一条U2本文消息 
	 * AB成为好友，同时发送A/B一条消息
	 * </pre>
	 * 
	 * @param siteUserId
	 * @param siteFriendId
	 */
	public void addFriendTextMessage(String siteUserId, String siteFriendId) {
		try {
			CoreProto.MsgText textMsg = CoreProto.MsgText.newBuilder().setMsgId(buildU2MsgId(siteUserId))
					.setSiteUserId(siteUserId).setSiteFriendId(siteFriendId)
					.setText(ByteString.copyFromUtf8(NoticeText.USER_ADD_FRIEND)).setTime(System.currentTimeMillis())
					.build();
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
					.setType(MsgType.TEXT).setText(textMsg).build();

			Command command = new Command();
			command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
			command.setSiteUserId(siteUserId);
			command.setSiteFriendId(siteFriendId);
			command.setParams(request.toByteArray());

			boolean result = u2MsgService.execute(command);
			logger.debug("add friend Text message siteUserId={} siteFriendId={} result={}", siteUserId, siteFriendId,
					result);
		} catch (Exception e) {
			logger.error(StringHelper.format("send add friend text message error. siteUserId={} siteFriendId={}",
					siteUserId, siteFriendId), e);
		}

		try {
			CoreProto.MsgText textMsg = CoreProto.MsgText.newBuilder().setMsgId(buildU2MsgId(siteFriendId))
					.setSiteUserId(siteFriendId).setSiteFriendId(siteUserId)
					.setText(ByteString.copyFromUtf8(NoticeText.USER_ADD_FRIEND)).setTime(System.currentTimeMillis())
					.build();
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
					.setType(MsgType.TEXT).setText(textMsg).build();

			Command command = new Command();
			command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
			command.setSiteUserId(siteFriendId);
			command.setSiteFriendId(siteUserId);
			command.setParams(request.toByteArray());

			boolean result = u2MsgService.execute(command);
			logger.debug("add friend Text message siteUserId={} siteFriendId={} result={}", siteFriendId, siteUserId,
					result);
		} catch (Exception e) {
			logger.error(StringHelper.format("send add friend text message error. siteUserId={} siteFriendId={}",
					siteFriendId, siteUserId), e);
		}
	}

	private String buildU2MsgId(String siteUserid) {
		StringBuilder sb = new StringBuilder("U2-");
		if (StringUtils.isNotEmpty(siteUserid)) {
			int len = siteUserid.length();
			sb.append(siteUserid.substring(0, len >= 8 ? 8 : len));
			sb.append("-");
		}
		sb.append(System.currentTimeMillis());
		return sb.toString();
	}
}
