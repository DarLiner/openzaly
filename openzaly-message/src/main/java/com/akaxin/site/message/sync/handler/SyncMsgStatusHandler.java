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
package com.akaxin.site.message.sync.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.site.ImSyncMsgStatusProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-26 20:30:30
 */
public class SyncMsgStatusHandler extends AbstractSyncHandler<Command> {
	private static Logger logger = LoggerFactory.getLogger(SyncMsgStatusHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			ImSyncMsgStatusProto.ImSyncMsgStatusRequest request = ImSyncMsgStatusProto.ImSyncMsgStatusRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			List<String> u2MsgIds = request.getU2MsgIdList();
			List<String> groupMsgIds = request.getGroupMsgIdList();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (u2MsgIds != null && u2MsgIds.size() > 0) {
				List<U2MessageBean> u2msgList = messageDao.queryU2MessageByMsgId(u2MsgIds);
				if (u2msgList != null) {
					for (U2MessageBean bean : u2msgList) {
						if (siteUserId.equals(bean.getSendUserId())) {
							msgStatusResponse(command, bean.getMsgId(), bean.getMsgTime(), true);
						}
					}
				}
			}

			if (groupMsgIds != null && groupMsgIds.size() > 0) {
				List<GroupMessageBean> gmsgList = messageDao.queryGroupMesageByMsgId(groupMsgIds);
				if (gmsgList != null) {
					for (GroupMessageBean bean : gmsgList) {
						if (siteUserId.equals(bean.getSendUserId())) {
							msgStatusResponse(command, bean.getMsgId(), bean.getMsgTime(), true);
						}
					}
				}
			}

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, e);
		}
		return false;
	}

}
