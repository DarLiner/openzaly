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

public class U2MessageImageHandler extends AbstractUserHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageImageHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();

		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());

			int type = request.getType().getNumber();

			if (CoreProto.MsgType.IMAGE_VALUE == type) {
				logger.info("Im.msg U2Message Image");

				String siteUserId = request.getImage().getSiteUserId();
				String siteFriendId = request.getImage().getSiteFriendId();
				String msgId = request.getImage().getMsgId();
				String imageId = request.getImage().getImageId();

				command.setSiteUserId(siteUserId);
				command.setSiteFriendId(siteFriendId);

				logger.info("Msg msgid:{} type:{} from:{} to:{} imageId:{}", msgId, type, siteUserId, siteFriendId,
						imageId);

				U2MessageBean u2Bean = new U2MessageBean();
				u2Bean.setMsgId(msgId);
				u2Bean.setMsgType(type);
				u2Bean.setSendUserId(siteUserId);
				u2Bean.setSiteUserId(siteFriendId);
				u2Bean.setContent(imageId);
				u2Bean.setMsgTime(System.currentTimeMillis());

				boolean saveRes = messageDao.saveU2Message(u2Bean);

				msgResponse(channelSession.getChannel(), command, siteUserId, siteFriendId, msgId);
				return saveRes;
			}

			return true;
		} catch (Exception e) {
			logger.error("message u2 Image error!", e);

		}

		return false;
	}

	private void msgResponse(Channel channel, Command command, String from, String to, String msgId) {

		logger.info("response to client from:{} to:{} msgId:{}", from, to, msgId);

		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgStatus(1).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(0, statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		channel.writeAndFlush(
				new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT).add(data.toByteArray()));

	}
}
