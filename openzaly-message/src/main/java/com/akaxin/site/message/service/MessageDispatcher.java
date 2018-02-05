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
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.executor.MessageExecutor;

public class MessageDispatcher {
	private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

	public static boolean dispatch(Command command) {
		logger.info("IM request dispatch command={}", command.toString());
		String action = command.getAction();
		if (action.equalsIgnoreCase(RequestAction.IM_CTS_MESSAGE.getName())) {
			try {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				logger.info("IM message dispatcher type:{0}", request.getType().getNumber());

				switch (request.getType().getNumber()) {
				case CoreProto.MsgType.NOTICE_VALUE:
					break;
				case CoreProto.MsgType.TEXT_VALUE:
				case CoreProto.MsgType.SECRET_TEXT_VALUE:
				case CoreProto.MsgType.IMAGE_VALUE:
				case CoreProto.MsgType.SECRET_IMAGE_VALUE:
				case CoreProto.MsgType.VOICE_VALUE:
				case CoreProto.MsgType.SECRET_VOICE_VALUE:
				case CoreProto.MsgType.MAP_VALUE:
				case CoreProto.MsgType.SECRET_MAP_VALUE:
				case CoreProto.MsgType.U2_NOTICE_VALUE:
					MessageExecutor.getExecutor().execute("im.cts.message.u2", command);
					break;
				case CoreProto.MsgType.GROUP_TEXT_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_TEXT_VALUE:
				case CoreProto.MsgType.GROUP_IMAGE_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_IMAGE_VALUE:
				case CoreProto.MsgType.GROUP_VOICE_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_VOICE_VALUE:
				case CoreProto.MsgType.GROUP_MAP_VALUE:
				case CoreProto.MsgType.GROUP_SECRET_MAP_VALUE:
				case CoreProto.MsgType.GROUP_NOTICE_VALUE:
					MessageExecutor.getExecutor().execute("im.cts.message.group", command);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				logger.error("message dispatch exception", e);
			}
		} else if (action.equalsIgnoreCase(RequestAction.IM_SYNC_MESSAGE.getName())) {
			MessageExecutor.getExecutor().execute("im.sync.message", command);
		} else if (action.equalsIgnoreCase(RequestAction.IM_SYNC_FINISH.getName())) {
			MessageExecutor.getExecutor().execute("im.sync.finish", command);
		}
		return true;
	}
}
