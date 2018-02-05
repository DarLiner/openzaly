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
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserGroupDao;
import com.akaxin.site.storage.bean.GroupProfileBean;

public class GroupDetectionHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupDetectionHandler.class);

	public boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();
		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());

			String siteUserId = null;
			String groupId = null;
			String gmsgId = null;
			int type = request.getType().getNumber();
			switch (type) {
			case CoreProto.MsgType.GROUP_TEXT_VALUE:
				siteUserId = request.getGroupText().getSiteUserId();
				gmsgId = request.getGroupText().getMsgId();
				groupId = request.getGroupText().getSiteGroupId();
			case CoreProto.MsgType.GROUP_SECRET_TEXT_VALUE:
				break;
			case CoreProto.MsgType.GROUP_IMAGE_VALUE:
				siteUserId = request.getGroupImage().getSiteUserId();
				gmsgId = request.getGroupImage().getMsgId();
				groupId = request.getGroupImage().getSiteGroupId();
				break;
			case CoreProto.MsgType.GROUP_SECRET_IMAGE_VALUE:
				break;
			case CoreProto.MsgType.GROUP_VOICE_VALUE:
				siteUserId = request.getGroupVoice().getSiteUserId();
				gmsgId = request.getGroupVoice().getMsgId();
				groupId = request.getGroupVoice().getSiteGroupId();
				break;
			case CoreProto.MsgType.GROUP_SECRET_VOICE_VALUE:
				break;
			case CoreProto.MsgType.GROUP_NOTICE_VALUE:
				return true;
			default:
				break;
			}
			command.setSiteGroupId(groupId);

			if (StringUtils.isEmpty(command.getSiteUserId()) || StringUtils.isEmpty(command.getSiteGroupId())) {
				return false;
			}

			if (checkGroupStatus(groupId) && isGroupMember(siteUserId, groupId)) {
				return true;
			} else {
				logger.info("user is not group member.user:{},group:{}", siteUserId, groupId);
				response(command, siteUserId, groupId, gmsgId);
			}

		} catch (Exception e) {
			logger.error("group detection error!", e);
		}

		return false;
	}

	private void response(Command command, String from, String to, String msgId) {
		logger.info("Group detection error response to client:{}", "用户不是群成员，不能发送消息");
		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgStatus(-2).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		ChannelWriter.writeByDeviceId(command.getDeviceId(),
				new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT).add(data.toByteArray()));
	}

	private boolean checkGroupStatus(String groupId) {
		try {
			GroupProfileBean bean = ImUserGroupDao.getInstance().getGroupProfile(groupId);
			if (bean != null && StringUtils.isNotBlank(bean.getGroupId())) {
				if (bean.getGroupStatus() != 0) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("check group status error", e);
		}
		return false;
	}

	private boolean isGroupMember(String siteUserId, String groupId) {
		return ImUserGroupDao.getInstance().isGroupMember(siteUserId, groupId);
	}
}
