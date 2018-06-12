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
import com.akaxin.site.message.bean.WebBean;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

public class U2MessageWebHandler extends AbstractU2Handler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageWebHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();
			// 获取是否web消息类型
			if (CoreProto.MsgType.U2_WEB_VALUE == type) {

				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String proxySiteUserId = request.getU2Web().getSiteUserId();
				String siteFriendId = request.getU2Web().getSiteFriendId();
				String msgId = request.getU2Web().getMsgId();
				String webCode = request.getU2Web().getWebCode();
				int webWidth = request.getU2Web().getWidth();
				int webHeight = request.getU2Web().getHeight();
				String hrefUrl = request.getU2Web().getHrefUrl();
				long msgTime = System.currentTimeMillis();

				WebBean webBean = new WebBean();
				webBean.setWebCode(webCode);
				webBean.setHeight(webHeight);
				webBean.setWidth(webWidth);
				webBean.setHrefUrl(hrefUrl);

				U2MessageBean bean = new U2MessageBean();
				bean.setMsgId(msgId);
				bean.setMsgType(type);
				bean.setSiteUserId(siteFriendId);
				bean.setSendUserId(command.isProxy() ? proxySiteUserId : siteUserId);
				bean.setReceiveUserId(siteFriendId);
				bean.setContent(webBean.toString());
				bean.setMsgTime(msgTime);

				LogUtils.requestDebugLog(logger, command, bean.toString());

				boolean success = messageDao.saveU2Message(bean);

				if (command.isProxy()) {
					U2MessageBean proxyBean = new U2MessageBean();
					proxyBean.setMsgId(msgId);
					proxyBean.setMsgType(type);
					proxyBean.setSiteUserId(proxySiteUserId);
					proxyBean.setSendUserId(proxySiteUserId);
					proxyBean.setReceiveUserId(siteFriendId);
					proxyBean.setContent(webBean.toString());
					proxyBean.setMsgTime(msgTime);
					messageDao.saveU2Message(proxyBean);
				}

				msgStatusResponse(command, msgId, msgTime, success);

				return success;
			}

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		return false;
	}

}
