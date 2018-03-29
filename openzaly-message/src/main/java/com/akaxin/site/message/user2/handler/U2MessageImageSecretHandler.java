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
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

import io.netty.channel.Channel;

public class U2MessageImageSecretHandler extends AbstractU2Handler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageImageSecretHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();

		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());
			int type = request.getType().getNumber();

			if (CoreProto.MsgType.SECRET_IMAGE_VALUE == type) {
				String siteUserId = request.getSecretImage().getSiteUserId();
				String siteFriendId = request.getSecretImage().getSiteFriendId();
				String msgId = request.getSecretImage().getMsgId();
				String tsKey = request.getSecretImage().getTsKey();
				String ts_device_id = request.getSecretImage().getSiteDeviceId();
				String imageId = request.getSecretImage().getImageId();
				command.setSiteUserId(siteUserId);
				command.setSiteFriendId(siteFriendId);

				U2MessageBean u2Bean = new U2MessageBean();
				u2Bean.setMsgId(msgId);
				u2Bean.setMsgType(type);
				u2Bean.setSendUserId(siteUserId);
				u2Bean.setSiteUserId(siteFriendId);
				u2Bean.setContent(imageId);
				u2Bean.setTsKey(tsKey);
				u2Bean.setDeviceId(ts_device_id);
				long msgTime = System.currentTimeMillis();
				u2Bean.setMsgTime(System.currentTimeMillis());

				LogUtils.requestDebugLog(logger, command, u2Bean.toString());

				boolean success = messageDao.saveU2Message(u2Bean);
				msgStatusResponse(command, msgId, msgTime, success);

				return success;
			}

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		return false;
	}

//	private void msgResponse(Channel channel, Command command, String from, String to, String msgId) {
//
//		logger.info("response to client msgid:{} from:{} to:{}", msgId, from, to);
//
//		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgStatus(1).build();
//
//		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
//				.setType(MsgType.MSG_STATUS).setStatus(status).build();
//
//		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
//				.addList(0, statusMsg).build();
//
//		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
//				.setData(request.toByteString()).build();
//
//		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
//				.add(data.toByteArray()));
//
//	}
}
