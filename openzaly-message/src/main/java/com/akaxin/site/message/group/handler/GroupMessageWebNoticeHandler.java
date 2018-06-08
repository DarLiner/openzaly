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
import com.akaxin.site.message.bean.WebBean;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 群web通知消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-24 20:32:32
 */
public class GroupMessageWebNoticeHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupMessageWebNoticeHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();
			// group web notice
			if (CoreProto.MsgType.GROUP_WEB_NOTICE_VALUE == type) {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String deviceId = command.getDeviceId();
				String proxySiteUserId = request.getGroupWebNotice().getSiteUserId();
				String gmsgId = request.getGroupWebNotice().getMsgId();
				String groupId = request.getGroupWebNotice().getSiteGroupId();

				WebBean webBean = new WebBean();
				webBean.setWebCode(request.getGroupWebNotice().getWebCode());
				webBean.setHrefUrl(request.getGroupWebNotice().getHrefUrl());
				webBean.setHeight(request.getGroupWebNotice().getHeight());

				long msgTime = System.currentTimeMillis();
				GroupMessageBean bean = new GroupMessageBean();
				bean.setMsgId(gmsgId);
				// bean.setSendUserId(siteUserId);
				bean.setSendUserId(command.isProxy() ? proxySiteUserId : siteUserId);
				bean.setSendDeviceId(deviceId);
				bean.setSiteGroupId(groupId);
				bean.setContent(webBean.toString());
				bean.setMsgType(type);
				bean.setMsgTime(msgTime);

				LogUtils.requestDebugLog(logger, command, bean.toString());

				boolean success = messageDao.saveGroupMessage(bean);
				msgStatusResponse(command, gmsgId, msgTime, success);
				return success;
			}
			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		return false;
	}

}