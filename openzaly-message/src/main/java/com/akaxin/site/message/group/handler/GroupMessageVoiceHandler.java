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
package com.akaxin.site.message.group.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 群语音消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-26 15:15:17
 */
public class GroupMessageVoiceHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupMessageVoiceHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();

			if (CoreProto.MsgType.GROUP_VOICE_VALUE == type) {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String deviceId = command.getDeviceId();
				String gmsgId = request.getGroupVoice().getMsgId();
				String groupId = request.getGroupVoice().getSiteGroupId();
				String groupVoiceId = request.getGroupVoice().getVoiceId();

				long msgTime = System.currentTimeMillis();
				GroupMessageBean gmsgBean = new GroupMessageBean();
				gmsgBean.setMsgId(gmsgId);
				gmsgBean.setSendUserId(siteUserId);
				gmsgBean.setSiteGroupId(groupId);
				gmsgBean.setSendDeviceId(deviceId);
				gmsgBean.setContent(groupVoiceId);
				gmsgBean.setMsgType(type);
				gmsgBean.setMsgTime(msgTime);

				LogUtils.requestDebugLog(logger, command, gmsgBean.toString());

				boolean success = messageDao.saveGroupMessage(gmsgBean);
				msgStatusResponse(command, gmsgId, msgTime, success);
				return success;
			}
			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		return false;
	}

	// private void msgResponse(Channel channel, Command command, String from,
	// String to, String msgId, long msgTime) {
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
