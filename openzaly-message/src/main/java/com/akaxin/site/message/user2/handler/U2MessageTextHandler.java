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

import java.nio.charset.Charset;

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
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;

public class U2MessageTextHandler extends AbstractUserHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageTextHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();
		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());

			int type = request.getType().getNumber();

			if (CoreProto.MsgType.TEXT_VALUE == type) {
				String siteUserId = command.getSiteUserId();
				String siteFriendId = request.getText().getSiteFriendId();
				String msgId = request.getText().getMsgId();
				ByteString byteStr = request.getText().getText();
				String msgText = byteStr.toString(Charset.forName("UTF-8"));
				command.setSiteFriendId(siteFriendId);

				logger.info("U2 Text message siteUserId={} siteFriendId={} msgid={} text={}", siteUserId, siteFriendId,
						msgId, msgText);
				long msgTime = System.currentTimeMillis();
				U2MessageBean u2Bean = new U2MessageBean();
				u2Bean.setMsgId(msgId);
				u2Bean.setMsgType(type);
				u2Bean.setSendUserId(siteUserId);
				u2Bean.setSiteUserId(siteFriendId);
				u2Bean.setContent(msgText);
				u2Bean.setMsgTime(msgTime);

				boolean saveRes = messageDao.saveU2Message(u2Bean);

				msgResponse(channelSession.getChannel(), command, siteUserId, siteFriendId, msgId, msgTime);
				// 消息保存成功，继续执行PHN，消息保存失败退出
				return saveRes;
			}

			return true;
		} catch (Exception e) {
			logger.error("siteUserId={} send U2 message error.", e);
		}

		return false;
	}

	private void msgResponse(Channel channel, Command command, String from, String to, String msgId, long msgTime) {

		logger.info("response msg to client from:{} to:{} msgId:{}", from, to, msgId);

		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgServerTime(msgTime)
				.setMsgStatus(1).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
				.add(data.toByteArray()));

	}
}
