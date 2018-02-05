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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.business.constant.NoticeText;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.message.api.IMessageService;
import com.akaxin.site.message.service.ImMessageService;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.google.protobuf.ByteString;

/**
 * 主要发送群主相关的消息通知
 * 
 * <pre>
 * 	1.新用户加入了群聊消息提醒
 * 	2.
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-02 10:23:00
 */
public class GroupNotice {
	private static final Logger logger = LoggerFactory.getLogger(GroupNotice.class);

	private IMessageService groupMsgService = new ImMessageService();

	/**
	 * 新用户加入了群聊 <br>
	 * eg：群聊天界面中，<张三 加入了群聊天>
	 */
	public void userAddGroupNotice(String siteUserId, String groupId, List<String> userIdList) {
		String noticeText = "";
		try {
			if (userIdList != null) {
				for (String userId : userIdList) {
					SimpleUserBean bean = UserProfileDao.getInstance().getSimpleProfileById(userId);
					String userName = bean.getUserName();
					noticeText += userName + ",";
				}
			}
			noticeText = noticeText + NoticeText.USER_ADD_GROUP;
			this.groupMsgNotice(siteUserId, groupId, noticeText);
		} catch (Exception e) {
			logger.error("new group member notice error. notice=" + noticeText, e);
		}
	}

	/**
	 * 往群组中发送通知消息<eg:王小王加入了群聊天>
	 * 
	 * @param groupId
	 *            发送通知的群
	 * @param siteUserId
	 *            备用字段，兼容官方号推送通知消息，发送方ID
	 * 
	 */
	private void groupMsgNotice(String siteUserId, String siteGroupId, String noticeText) {
		logger.info("group msg notice siteUserId={},siteGroupId={},text={}", siteUserId, siteGroupId, noticeText);
		CoreProto.GroupMsgNotice groupMsgNotice = CoreProto.GroupMsgNotice.newBuilder().setSiteGroupId(siteGroupId)
				.setText(ByteString.copyFromUtf8(noticeText)).build();
		ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
				.setType(CoreProto.MsgType.GROUP_NOTICE).setGroupMsgNotice(groupMsgNotice).build();
		Command command = new Command();
		command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
		command.setSiteUserId(siteUserId);
		command.setSiteGroupId(siteGroupId);
		command.setParams(request.toByteArray());
		logger.info("group msg notice command={}", command.toString());
		groupMsgService.execute(command);
	}

}
