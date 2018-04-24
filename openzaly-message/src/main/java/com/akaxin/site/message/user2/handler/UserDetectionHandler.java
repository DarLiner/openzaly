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
package com.akaxin.site.message.user2.handler;

import org.apache.commons.lang3.StringUtils;
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
 * <pre>
 * 	消息发送之前的检测： 
 * 		1.群是否为存在的群 
 * 		2.如果用户之间不是好友，消息发送不出去
 * </pre>
 * 
 * @author Sam
 * @since 2017.11.02
 */
public class UserDetectionHandler extends AbstractU2Handler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(UserDetectionHandler.class);

	@Override
	public Boolean handle(Command command) {
		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());

			String siteUserId = null;
			String siteFriendId = null;
			String msgId = null;
			int type = request.getType().getNumber();
			switch (type) {
			case CoreProto.MsgType.TEXT_VALUE:
				siteUserId = request.getText().getSiteUserId();
				siteFriendId = request.getText().getSiteFriendId();
				msgId = request.getText().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_TEXT_VALUE:
				siteUserId = request.getSecretText().getSiteUserId();
				siteFriendId = request.getSecretText().getSiteFriendId();
				msgId = request.getSecretText().getMsgId();
				break;
			case CoreProto.MsgType.IMAGE_VALUE:
				siteUserId = request.getImage().getSiteUserId();
				siteFriendId = request.getImage().getSiteFriendId();
				msgId = request.getImage().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_IMAGE_VALUE:
				siteUserId = request.getSecretImage().getSiteUserId();
				siteFriendId = request.getSecretImage().getSiteFriendId();
				msgId = request.getSecretImage().getMsgId();
				break;
			case CoreProto.MsgType.VOICE_VALUE:
				siteUserId = request.getVoice().getSiteUserId();
				siteFriendId = request.getVoice().getSiteFriendId();
				msgId = request.getVoice().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_VOICE_VALUE:
				siteUserId = request.getSecretVoice().getSiteUserId();
				siteFriendId = request.getSecretVoice().getSiteFriendId();
				msgId = request.getSecretVoice().getMsgId();
				break;
			case CoreProto.MsgType.U2_NOTICE_VALUE:
				break;
			default:
				break;
			}

			command.setSiteUserId(siteUserId);
			command.setSiteFriendId(siteFriendId);
			command.setMsgType(type);

			if (checkUser(siteUserId, siteFriendId)) {
				return true;
			} else {
				logger.warn("user2 are not friend.user:{},friend:{}", siteUserId, siteFriendId);
				int statusValue = -1;// 非好友关系，返回状态值
				msgStatusResponse(command, msgId, System.currentTimeMillis(), statusValue);
			}

		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}
		return false;
	}

	/**
	 * <pre>
	 * 检测消息是否可以发送:
	 * 		1.消息接受者是否为正常用户
	 * 		2.二者是否为好友关系
	 * </pre>
	 * 
	 * @param siteUserId
	 * @param siteFriendId
	 * @return
	 */
	private boolean checkUser(String siteUserId, String siteFriendId) {
		try {
			SimpleUserBean bean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteFriendId);
			if (bean != null && StringUtils.isNotEmpty(bean.getUserId())) {
				if (bean.getUserStatus() != UserProto.UserStatus.NORMAL_VALUE) {
					return false;
				}
			} else {
				return false;
			}
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
