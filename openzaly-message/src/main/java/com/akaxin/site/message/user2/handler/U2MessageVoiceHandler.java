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
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 二人语音消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-26 15:10:20
 */
public class U2MessageVoiceHandler extends AbstractU2Handler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageVoiceHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();

			if (CoreProto.MsgType.VOICE_VALUE == type) {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String proxySiteUserId = request.getVoice().getSiteUserId();
				String siteFriendId = request.getVoice().getSiteFriendId();
				String msgId = request.getVoice().getMsgId();
				String voiceId = request.getVoice().getVoiceId();

				U2MessageBean u2Bean = new U2MessageBean();
				u2Bean.setMsgId(msgId);
				u2Bean.setMsgType(type);
				u2Bean.setSiteUserId(siteFriendId);
				u2Bean.setSendUserId(command.isProxy() ? proxySiteUserId : siteUserId);
				u2Bean.setReceiveUserId(siteFriendId);
				u2Bean.setContent(voiceId);
				long msgTime = System.currentTimeMillis();
				u2Bean.setMsgTime(msgTime);

				LogUtils.requestDebugLog(logger, command, u2Bean.toString());

				boolean success = messageDao.saveU2Message(u2Bean);

				if (command.isProxy()) {
					U2MessageBean proxyBean = new U2MessageBean();
					proxyBean.setMsgId(msgId);
					proxyBean.setMsgType(type);
					proxyBean.setSiteUserId(proxySiteUserId);
					proxyBean.setSendUserId(proxySiteUserId);
					proxyBean.setReceiveUserId(siteFriendId);
					proxyBean.setContent(voiceId);
					proxyBean.setMsgTime(msgTime);
					messageDao.saveU2Message(proxyBean);
				}

				msgStatusResponse(command, msgId, msgTime, success);
				return success;
			}

			return true;
		} catch (Exception e) {
			logger.error("U2 Message Voice error!", e);
		}

		return false;
	}

	// private void msgResponse(Channel channel, Command command, String from,
	// String to, String msgId, long msgTime) {
	//
	// logger.info("response to client msgId:{} from:{} to:{}", msgId, from, to);
	//
	// CoreProto.MsgStatus status =
	// CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgServerTime(msgTime)
	// .setMsgStatus(1).build();
	//
	// ImStcMessageProto.MsgWithPointer statusMsg =
	// ImStcMessageProto.MsgWithPointer.newBuilder()
	// .setType(MsgType.MSG_STATUS).setStatus(status).build();
	//
	// ImStcMessageProto.ImStcMessageRequest request =
	// ImStcMessageProto.ImStcMessageRequest.newBuilder()
	// .addList(0, statusMsg).build();
	//
	// CoreProto.TransportPackageData data =
	// CoreProto.TransportPackageData.newBuilder()
	// .setData(request.toByteString()).build();
	//
	// channel.writeAndFlush(new
	// RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
	// .add(data.toByteArray()));
	//
	// }
}
