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

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

import io.netty.channel.Channel;

public class U2MessageVoiceSecretHandler extends AbstractUserHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageVoiceSecretHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();

		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());

			int type = request.getType().getNumber();

			if (CoreProto.MsgType.SECRET_VOICE_VALUE == type) {
				logger.info("U2 Message SecretVoice type={}", type);

				String site_user_id = request.getSecretVoice().getSiteUserId();
				String site_friend_id = request.getSecretVoice().getSiteFriendId();
				String msg_id = request.getSecretVoice().getMsgId();
				String ts_key = request.getSecretVoice().getTsKey();
				String ts_device_id = request.getSecretVoice().getSiteDeviceId();
				String secretVoiceId = request.getSecretVoice().getVoicdId();

				command.setSiteUserId(site_user_id);
				command.setSiteFriendId(site_friend_id);

				long msgTime = System.currentTimeMillis();
				U2MessageBean u2Bean = new U2MessageBean();
				u2Bean.setMsgId(msg_id);
				u2Bean.setMsgType(type);
				u2Bean.setSendUserId(site_user_id);
				u2Bean.setSiteUserId(site_friend_id);
				u2Bean.setContent(secretVoiceId);
				u2Bean.setTsKey(ts_key);
				u2Bean.setDeviceId(ts_device_id);
				u2Bean.setMsgTime(msgTime);

				logger.info("U2 Message SecretVoice bean={}", u2Bean.toString());

				boolean saveRes = messageDao.saveU2Message(u2Bean);

				msgResponse(channelSession.getChannel(), command, site_user_id, site_friend_id, msg_id, msgTime);

				return saveRes;
			}

			return true;
		} catch (Exception e) {
			logger.error("U2 Message Secret Voice error!", e);
		}

		return false;
	}

	private void msgResponse(Channel channel, Command command, String from, String to, String msgId, long msgTime) {
		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgServerTime(msgTime)
				.setMsgStatus(1).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(0, statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
				.add(data.toByteArray()));

	}
}
