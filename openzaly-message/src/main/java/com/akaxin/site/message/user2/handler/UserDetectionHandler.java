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
 * 	U2消息发送之前的检测： 
 * 		1.支持消息属于代发类型
 * 		2.发送者和接受者是否为正常状态用户
 * 		3.如果用户之间不是好友，消息发送不出去
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
			String siteUserId = command.getSiteUserId();
			String siteFriendId = null;
			String msgId = null;
			int type = request.getType().getNumber();
			command.setMsgType(type);

			switch (type) {
			case CoreProto.MsgType.TEXT_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getText().getSiteUserId();
				}
				siteFriendId = request.getText().getSiteFriendId();
				msgId = request.getText().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_TEXT_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getSecretText().getSiteUserId();
				}
				siteFriendId = request.getSecretText().getSiteFriendId();
				msgId = request.getSecretText().getMsgId();
				break;
			case CoreProto.MsgType.IMAGE_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getImage().getSiteUserId();
				}
				siteFriendId = request.getImage().getSiteFriendId();
				msgId = request.getImage().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_IMAGE_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getSecretImage().getSiteUserId();
				}
				siteFriendId = request.getSecretImage().getSiteFriendId();
				msgId = request.getSecretImage().getMsgId();
				break;
			case CoreProto.MsgType.VOICE_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getVoice().getSiteUserId();
				}
				siteFriendId = request.getVoice().getSiteFriendId();
				msgId = request.getVoice().getMsgId();
				break;
			case CoreProto.MsgType.SECRET_VOICE_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getSecretVoice().getSiteUserId();
				}
				siteFriendId = request.getSecretVoice().getSiteFriendId();
				msgId = request.getSecretVoice().getMsgId();
				break;
			case CoreProto.MsgType.U2_NOTICE_VALUE:
				// 通知消息不需要返回response
				command.setMsgType(type);
				return true;
			case CoreProto.MsgType.U2_WEB_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getU2Web().getSiteUserId();
				}
				siteFriendId = request.getU2Web().getSiteFriendId();
				command.setProxySiteUserId(siteUserId);
				command.setSiteFriendId(siteFriendId);
				return true;
			case CoreProto.MsgType.U2_WEB_NOTICE_VALUE:
				if (command.isProxy()) {
					siteUserId = request.getU2WebNotice().getSiteUserId();
					siteFriendId = request.getU2WebNotice().getSiteFriendId();
					command.setProxySiteUserId(siteUserId);
					command.setSiteFriendId(siteFriendId);
				}
				return true;
			default:
				logger.error("error message type cmd={} request={}", command.toString(), request.toString());
				return false;
			}
			command.setProxySiteUserId(siteUserId);
			command.setSiteFriendId(siteFriendId);

			if (checkUser(siteUserId, siteFriendId, command.isMasterDB())) {
				return true;
			} else {
				logger.warn("users arent friend relation.siteUserId:{},siteFriendId:{}", siteUserId, siteFriendId);
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
	 * 		1.消息发送者是否为正常用户
	 * 		2.消息接受者是否为正常用户
	 * 		3.二者是否为好友关系
	 * </pre>
	 * 
	 * @param siteUserId
	 * @param siteFriendId
	 * @return
	 */
	private boolean checkUser(String siteUserId, String siteFriendId, boolean isMaster) {
		try {
			// 检测发送者的状态
			SimpleUserBean userBean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteUserId, isMaster);
			if (userBean != null) {
				if (userBean.getUserStatus() != UserProto.UserStatus.NORMAL_VALUE) {
					return false;
				}
			} else {
				return false;
			}

			// 检测接受者的状态
			SimpleUserBean friendBean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteFriendId, isMaster);
			if (friendBean != null) {
				if (friendBean.getUserStatus() != UserProto.UserStatus.NORMAL_VALUE) {
					return false;
				}
			} else {
				return false;
			}

			// 检测是否为好友关系
			if (!ImUserFriendDao.getInstance().isFriend(siteUserId, siteFriendId, isMaster)) {
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
