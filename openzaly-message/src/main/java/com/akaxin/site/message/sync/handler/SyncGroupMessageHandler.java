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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImSyncMessageProto;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.akaxin.site.storage.service.MessageDaoService;
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;

public class SyncGroupMessageHandler extends AbstractSyncHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(SyncGroupMessageHandler.class);
	private static final int SYNC_MAX_MESSAGE_COUNT = 100;
	private IGroupDao userGroupDao = new GroupDaoService();
	private IMessageDao syncDao = new MessageDaoService();

	public boolean handle(Command command) {
		int syncCount = 0;
		ChannelSession channelSession = command.getChannelSession();
		try {
			ImSyncMessageProto.ImSyncMessageRequest syncRequest = ImSyncMessageProto.ImSyncMessageRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String deviceId = command.getDeviceId();
			Map<String, Long> groupPointerMap = syncRequest.getGroupsPointerMap();
			logger.info("[Start sync group] siteUserId={} deviceId={} request={}", siteUserId, deviceId,
					syncRequest.toString());

			// 查找个人有多少群
			List<String> userGroups = userGroupDao.getUserGroupsId(siteUserId);
			if (userGroups != null) {
				for (String groupId : userGroups) {
					long gpointer = 0;
					if (groupPointerMap != null && groupPointerMap.get(groupId) != null) {
						gpointer = groupPointerMap.get(groupId);
					}
					logger.info("[Syncing group={}] siteUserId={} deviceId={} gpointer={}", groupId, siteUserId,
							deviceId, gpointer);
					List<GroupMessageBean> groupMessageList = syncDao.queryGroupMessage(groupId, siteUserId, deviceId,
							gpointer);
					if (groupMessageList != null && groupMessageList.size() > 0) {
						syncCount += groupMessageList.size();
						groupMessageToClient(channelSession.getChannel(), siteUserId, groupMessageList);
					}
					logger.info("[Syncing group={}] syncSize={}", groupId, groupMessageList.size());
				}
			}
			logger.info("[End sync group] siteUserId={} deviceId={} syncCount={}", siteUserId, deviceId, syncCount);

			msgFinishToClient(channelSession.getChannel(), siteUserId, deviceId);

		} catch (Exception e) {
			logger.error("sync group message error.", e);
		}

		return true;
	}

	private void groupMessageToClient(Channel channel, String userId, List<GroupMessageBean> groupMessageList) {
		ImStcMessageProto.ImStcMessageRequest.Builder requestBuilder = ImStcMessageProto.ImStcMessageRequest
				.newBuilder();
		for (GroupMessageBean gmsgBean : groupMessageList) {
			logger.info("[Syncing group={}] OK. bean={}", gmsgBean.getSiteGroupId(), gmsgBean.toString());
			switch (gmsgBean.getMsgType()) {
			case CoreProto.MsgType.GROUP_TEXT_VALUE:
				try {
					CoreProto.GroupText groupText = CoreProto.GroupText.newBuilder().setMsgId(gmsgBean.getMsgId())
							.setSiteUserId(gmsgBean.getSendUserId()).setSiteGroupId(gmsgBean.getSiteGroupId())
							.setText(ByteString.copyFromUtf8(gmsgBean.getContent())).setTime(gmsgBean.getMsgTime())
							.build();
					ImStcMessageProto.MsgWithPointer gmsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.GROUP_TEXT).setPointer(gmsgBean.getId()).setGroupText(groupText).build();
					requestBuilder.addList(gmsg);
				} catch (Exception et) {
					logger.error("sync group text messge error", et);
				}
				break;

			case CoreProto.MsgType.GROUP_SECRET_TEXT_VALUE:
				// do nothing
				break;
			case CoreProto.MsgType.GROUP_IMAGE_VALUE:
				try {
					CoreProto.GroupImage groupImage = CoreProto.GroupImage.newBuilder().setMsgId(gmsgBean.getMsgId())
							.setSiteUserId(gmsgBean.getSendUserId()).setSiteGroupId(gmsgBean.getSiteGroupId())
							.setImageId(gmsgBean.getContent()).setTime(gmsgBean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer groupImageMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.GROUP_IMAGE).setPointer(gmsgBean.getId()).setGroupImage(groupImage)
							.build();
					requestBuilder.addList(groupImageMsg);
				} catch (Exception egi) {
					logger.error("sync group image message error.", egi);
				}
				break;
			case CoreProto.MsgType.GROUP_SECRET_IMAGE_VALUE:
				// do nothing
				break;
			case CoreProto.MsgType.GROUP_VOICE_VALUE:
				try {
					try {
						CoreProto.GroupVoice groupVoice = CoreProto.GroupVoice.newBuilder()
								.setMsgId(gmsgBean.getMsgId()).setSiteUserId(gmsgBean.getSendUserId())
								.setSiteGroupId(gmsgBean.getSiteGroupId()).setVoiceId(gmsgBean.getContent())
								.setTime(gmsgBean.getMsgTime()).build();
						ImStcMessageProto.MsgWithPointer groupVoiceMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
								.setType(MsgType.GROUP_VOICE).setPointer(gmsgBean.getId()).setGroupVoice(groupVoice)
								.build();
						requestBuilder.addList(groupVoiceMsg);
					} catch (Exception egv) {
						logger.error("sync group voice message error.", egv);
					}
				} catch (Exception egv) {
					logger.error("sync group voice message error.", egv);
				}
				break;
			case CoreProto.MsgType.GROUP_SECRET_VOICE_VALUE:
				// do nothing
				break;
			}
		}

		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		ImStcMessageProto.ImStcMessageRequest request = requestBuilder.build();
		CoreProto.TransportPackageData datas = CoreProto.TransportPackageData.newBuilder().putAllHeader(header)
				.setData(ByteString.copyFrom(request.toByteArray())).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
				.add(datas.toByteArray()));

	}

	private void msgFinishToClient(Channel channel, String siteUserId, String deviceId) {
		logger.info("[Start Msg Finish] siteUserId={} deviceId={}", siteUserId, deviceId);

		CoreProto.MsgFinish msgFinish = CoreProto.MsgFinish.newBuilder().build();
		ImStcMessageProto.MsgWithPointer msgWithFinish = ImStcMessageProto.MsgWithPointer.newBuilder()
				.setType(MsgType.MSG_FINISH).setFinish(msgFinish).build();

		ImStcMessageProto.ImStcMessageRequest request = ImStcMessageProto.ImStcMessageRequest.newBuilder()
				.addList(msgWithFinish).build();

		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder().putAllHeader(header)
				.setData(ByteString.copyFrom(request.toByteArray())).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_FINISH)
				.add(data.toByteArray()));
		logger.info("[End Msg Finish] siteUserId={} deviceId={}", siteUserId, deviceId);
	}

}
