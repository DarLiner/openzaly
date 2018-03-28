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

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.service.MessageDaoService;
import com.google.protobuf.InvalidProtocolBufferException;

public class GroupMsgNoticeHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupMsgNoticeHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	@Override
	public Boolean handle(Command command) {
		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());
			int type = request.getType().getNumber();
			if (CoreProto.MsgType.GROUP_NOTICE_VALUE == type) {
				String siteUserId = command.getSiteUserId();
				String deviceId = command.getDeviceId();
				String groupId = request.getGroupMsgNotice().getSiteGroupId();
				String groupNoticeText = request.getGroupMsgNotice().getText().toStringUtf8();
				
				GroupMessageBean bean = new GroupMessageBean();
				bean.setSendUserId(siteUserId);
				bean.setSendDeviceId(deviceId);
				bean.setSiteGroupId(groupId);
				bean.setContent(groupNoticeText);
				bean.setMsgType(type);
				bean.setMsgTime(System.currentTimeMillis());

				logger.info("Group Msg Notice bean={}", bean.toString());

				messageDao.saveGroupMessage(bean);
			}
			return true;
		} catch (InvalidProtocolBufferException e) {
			logger.error("group msg notice protobuffer error.", e);
		} catch (SQLException e) {
			logger.error("save group msg notice to database error.", e);
		}
		return false;
	}

}
