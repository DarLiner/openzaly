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
 * 群web消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-24 20:32:13
 */
public class GroupMessageWebHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupMessageWebHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();

			if (CoreProto.MsgType.GROUP_WEB_VALUE == type) {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String deviceId = command.getDeviceId();

				String proxySiteUserId = request.getGroupWeb().getSiteUserId();
				String gmsgId = request.getGroupWeb().getMsgId();
				String groupId = request.getGroupWeb().getSiteGroupId();
				String webCode = request.getGroupWeb().getWebCode();
				int webWidth = request.getGroupWeb().getWidth();
				int webHeight = request.getGroupWeb().getHeight();
				long msgTime = System.currentTimeMillis();

				WebBean webBean = new WebBean();
				webBean.setWebCode(webCode);
				webBean.setHeight(webHeight);
				webBean.setWidth(webWidth);

				GroupMessageBean gmsgBean = new GroupMessageBean();
				gmsgBean.setMsgId(gmsgId);
//				gmsgBean.setSendUserId(siteUserId);
				gmsgBean.setSendUserId(command.isProxy() ? proxySiteUserId : siteUserId);
				gmsgBean.setSendDeviceId(deviceId);
				gmsgBean.setSiteGroupId(groupId);
				gmsgBean.setContent(webBean.toString());
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
	// .addList(statusMsg).build();
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
