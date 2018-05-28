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
package com.akaxin.site.message.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.executor.MessageExecutor;

public class MessageDispatcher {
	private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

	public static boolean dispatch(Command command) {
		String action = command.getAction();
		LogUtils.requestDebugLog(logger, command, "");

		if (RequestAction.IM_CTS_MESSAGE.getName().equalsIgnoreCase(action)) {
			try {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());

				switch (request.getType().getNumber()) {
				case CoreProto.MsgType.NOTICE_VALUE:
					break;
				case CoreProto.MsgType.TEXT_VALUE:
				case CoreProto.MsgType.SECRET_TEXT_VALUE:
				case CoreProto.MsgType.IMAGE_VALUE:
				case CoreProto.MsgType.SECRET_IMAGE_VALUE:
				case CoreProto.MsgType.VOICE_VALUE:
				case CoreProto.MsgType.SECRET_VOICE_VALUE:
				case CoreProto.MsgType.U2_MAP_VALUE:
				case CoreProto.MsgType.U2_SECRET_MAP_VALUE:
				case CoreProto.MsgType.U2_NOTICE_VALUE:
				case CoreProto.MsgType.U2_WEB_VALUE:
				case CoreProto.MsgType.U2_WEB_NOTICE_VALUE:
					MessageExecutor.getExecutor().execute("im.cts.message.u2", command);
					return true;
				case CoreProto.MsgType.GROUP_TEXT_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_TEXT_VALUE:
				case CoreProto.MsgType.GROUP_IMAGE_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_IMAGE_VALUE:
				case CoreProto.MsgType.GROUP_VOICE_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_VOICE_VALUE:
				case CoreProto.MsgType.GROUP_MAP_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_MAP_VALUE:
				case CoreProto.MsgType.GROUP_NOTICE_VALUE:
				case CoreProto.MsgType.GROUP_WEB_VALUE:
				case CoreProto.MsgType.GROUP_WEB_NOTICE_VALUE:
					MessageExecutor.getExecutor().execute("im.cts.message.group", command);
					return true;
				}
			} catch (Exception e) {
				logger.error(StringHelper.format("client={} siteUserId={} action={} im message dispatch error",
						command.getClientIp(), command.getSiteUserId(), command.getAction()), e);
			}
		} else if (RequestAction.IM_SYNC_MESSAGE.getName().equalsIgnoreCase(action)) {
			MessageExecutor.getExecutor().execute(RequestAction.IM_SYNC_MESSAGE.getName(), command);
			return true;
		} else if (RequestAction.IM_SYNC_FINISH.getName().equalsIgnoreCase(action)) {
			MessageExecutor.getExecutor().execute(RequestAction.IM_SYNC_FINISH.getName(), command);
			return true;
		} else if (RequestAction.IM_SYNC_MSGSTATUS.getName().equalsIgnoreCase(action)) {
			MessageExecutor.getExecutor().execute(RequestAction.IM_SYNC_MSGSTATUS.getName(), command);
			return true;
		} else if (RequestAction.IM_STC_NOTICE.getName().equalsIgnoreCase(action)) {
			MessageExecutor.getExecutor().execute(RequestAction.IM_STC_NOTICE.getName(), command);
			return true;
		}

		logger.error("client={} siteUserId={} action={} im message with error command={}", command.getClientIp(),
				command.getSiteUserId(), command.getAction(), command.toString());
		return false;
	}
}
