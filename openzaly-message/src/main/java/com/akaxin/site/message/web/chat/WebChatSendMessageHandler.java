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
package com.akaxin.site.message.web.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserFriendDao;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;

/**
 * web消息检测
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-08 18:34:00
 */
public class WebChatSendMessageHandler {
	private static final Logger logger = LoggerFactory.getLogger(WebChatSendMessageHandler.class);

	public Boolean handle(Command command) {
		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String siteFriendId = null;
			String msgId = null;
			int type = request.getType().getNumber();
			command.setMsgType(type);

			switch (type) {
			case CoreProto.MsgType.TEXT_VALUE:
				siteFriendId = request.getText().getSiteFriendId();
				msgId = request.getText().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_TEXT_VALUE:
				siteFriendId = request.getSecretText().getSiteFriendId();
				msgId = request.getSecretText().getMsgId();
				break;
			case CoreProto.MsgType.IMAGE_VALUE:
				siteFriendId = request.getImage().getSiteFriendId();
				msgId = request.getImage().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_IMAGE_VALUE:
				siteFriendId = request.getSecretImage().getSiteFriendId();
				msgId = request.getSecretImage().getMsgId();
				break;
			case CoreProto.MsgType.VOICE_VALUE:
				siteFriendId = request.getVoice().getSiteFriendId();
				msgId = request.getVoice().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_VOICE_VALUE:
				siteFriendId = request.getSecretVoice().getSiteFriendId();
				msgId = request.getSecretVoice().getMsgId();
				break;
			case CoreProto.MsgType.U2_NOTICE_VALUE:
				// 通知消息不需要返回response
				command.setMsgType(type);
				return true;
			case CoreProto.MsgType.U2_WEB_VALUE:
				return true;
			case CoreProto.MsgType.U2_WEB_NOTICE_VALUE:
				return true;
			default:
				logger.error("it's a unsupport type message cmd={} request={}", command.toString(), request.toString());
				return false;
			}

			command.setSiteFriendId(siteFriendId);

			if (checkUser(siteUserId, siteFriendId)) {
				return true;
			} else {
				logger.warn("user2 are not friend.user:{},friend:{}", siteUserId, siteFriendId);
				int statusValue = -1;// 非好友关系，返回状态值
				// msgStatusResponse(command, msgId, System.currentTimeMillis(), statusValue);
			}

		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}
		return false;
	}

	/**
	 * <pre>
	 * 检测消息是否可以发送:
	 * 		1.消息发送者是否为正常用户
	 * 		2.消息接受者是否为正常用户
	 * 		3.二者是否为好友关系
	 * </pre>
	 * 
	 * @param siteUserId
	 * @param siteFriendId
	 * @return
	 */
	private boolean checkUser(String siteUserId, String siteFriendId) {
		try {
			// 检测发送者的状态
			SimpleUserBean userBean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteUserId);
			if (userBean != null) {
				if (userBean.getUserStatus() != UserProto.UserStatus.NORMAL_VALUE) {
					return false;
				}
			} else {
				return false;
			}

			// 检测接受者的状态
			SimpleUserBean friendBean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteFriendId);
			if (friendBean != null) {
				if (friendBean.getUserStatus() != UserProto.UserStatus.NORMAL_VALUE) {
					return false;
				}
			} else {
				return false;
			}

			// 检测是否为好友关系
			if (!ImUserFriendDao.getInstance().isFriend(siteUserId, siteFriendId)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error(StringHelper.format("check siteUserid={} siteFriendId={} error.", siteUserId, siteFriendId),
					e);
		}
		return false;
	}

}
