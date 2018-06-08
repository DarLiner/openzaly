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

import org.apache.commons.lang3.StringUtils;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.executor.chain.handler.IHandler;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;

import io.netty.channel.Channel;

public abstract class AbstractU2Handler<T> implements IHandler<T, Boolean> {

	/**
	 * 生成msgId
	 * 
	 * @param siteUserid
	 * @return
	 */
	protected String buildU2MsgId(String siteUserid) {
		StringBuilder sb = new StringBuilder("U2-");
		if (StringUtils.isNotEmpty(siteUserid)) {
			int len = siteUserid.length();
			sb.append(siteUserid.substring(0, len >= 8 ? 8 : len));
			sb.append("-");
		}
		sb.append(System.currentTimeMillis());
		return sb.toString();
	}

	/**
	 * <pre>
	 * 	status=1:发送成功
	 * 	status=0：默认发送失败状态
	 * 	status=-1:非好友，不能发送
	 * 	status=-2:非群成员，发送失败
	 * </pre>
	 */
	protected void msgStatusResponse(Command command, String msgId, long msgTime, boolean success) {
		if (command == null || StringUtils.isEmpty(command.getDeviceId())) {
			return;
		}
		int statusValue = success ? 1 : 0;

		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgServerTime(msgTime)
				.setMsgStatus(statusValue).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		ChannelWriter.writeByDeviceId(command.getDeviceId(), new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
				.add(CommandConst.IM_MSG_TOCLIENT).add(data.toByteArray()));

	}

	protected void msgStatusResponse(Command command, String msgId, long msgTime, int statusValue) {
		if (command == null || StringUtils.isAnyEmpty(command.getDeviceId(), msgId)) {
			return;
		}
		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgStatus(statusValue)
				.setMsgServerTime(msgTime).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		ChannelWriter.writeByDeviceId(command.getDeviceId(), new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
				.add(CommandConst.IM_MSG_TOCLIENT).add(data.toByteArray()));
	}

	/**
	 * 通过channel回执消息状态
	 */
	@Deprecated
	protected void msgStatusResponse(Channel channel, Command command, String from, String to, String msgId,
			long msgTime) {
		CoreProto.MsgStatus status = CoreProto.MsgStatus.newBuilder().setMsgId(msgId).setMsgServerTime(msgTime)
				.setMsgStatus(1).build();

		ImStcMessageProto.MsgWithPointer statusMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_STATUS).setStatus(status).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(statusMsg).build();

		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder()
				.setData(request.toByteString()).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
				.add(data.toByteArray()));

	}
}
