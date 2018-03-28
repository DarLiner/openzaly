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

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserFriendDao;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;

import io.netty.channel.Channel;

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
public class UserDetectionHandler extends AbstractUserHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(UserDetectionHandler.class);

	@Override
	public Boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();
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
				return true;
			default:
				break;
			}

			command.setSiteUserId(siteUserId);
			command.setSiteFriendId(siteFriendId);

			if (checkUser(siteUserId, siteFriendId)) {
				logger.info("siteUserId={} can send message to siteFriendId={}", siteUserId, siteFriendId);
				return true;
			} else {
				logger.warn("user2 are not friend.user:{},friend:{}", siteUserId, siteFriendId);
				response(channelSession.getChannel(), command, siteUserId, siteFriendId, msgId);
			}

		} catch (Exception e) {
			logger.error("UserDetectionHandler error.", e);
		}
		return false;
	}

	/**
	 * 如果互相不为好友，则消息发送失败，回执给客户端
	 */
	private void response(Channel channel, Command command, String from, String to, String msgId) {
		logger.info("二者非好友关系，消息发送失败回执 from={} to={}", from, to);
		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgStatus(-1).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		ChannelWriter.writeByDeviceId(command.getDeviceId(), new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
				.add(CommandConst.IM_MSG_TOCLIENT).add(data.toByteArray()));
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
			logger.error("check user error.", e);
		}
		return false;
	}
}
