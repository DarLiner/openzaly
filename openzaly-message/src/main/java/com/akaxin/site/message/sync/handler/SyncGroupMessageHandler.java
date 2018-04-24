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
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImSyncMessageProto;
import com.akaxin.site.message.bean.WebBean;
import com.akaxin.site.message.utils.NumUtils;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.akaxin.site.storage.service.MessageDaoService;
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;

/**
 * 同步群组消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-24 20:47:59
 */
public class SyncGroupMessageHandler extends AbstractSyncHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(SyncGroupMessageHandler.class);
	private static final int SYNC_MAX_MESSAGE_COUNT = 100;
	private IGroupDao userGroupDao = new GroupDaoService();
	private IMessageDao syncDao = new MessageDaoService();

	public Boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();
		try {
			ImSyncMessageProto.ImSyncMessageRequest syncRequest = ImSyncMessageProto.ImSyncMessageRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String deviceId = command.getDeviceId();
			Map<String, Long> groupPointerMap = syncRequest.getGroupsPointerMap();

			LogUtils.requestDebugLog(logger, command, syncRequest.toString());

			int syncTotalCount = 0;
			// 查找个人有多少群
			List<String> userGroups = userGroupDao.getUserGroupsId(siteUserId);
			if (userGroups != null) {
				for (String groupId : userGroups) {
					long clientGroupPointer = 0;
					if (groupPointerMap != null && groupPointerMap.get(groupId) != null) {
						clientGroupPointer = groupPointerMap.get(groupId);
					}
					// 校验群消息游标
					long maxGroupMessagePointer = syncDao.queryGroupMessagePointer(groupId, siteUserId, deviceId);
					long startPointer = NumUtils.getMax(maxGroupMessagePointer, clientGroupPointer);

					while (true) {
						List<GroupMessageBean> msgList = syncDao.queryGroupMessage(groupId, siteUserId, deviceId,
								startPointer, SYNC_MAX_MESSAGE_COUNT);

						if (msgList != null && msgList.size() > 0) {
							startPointer = groupMessageToClient(channelSession.getChannel(), siteUserId, msgList);
							syncTotalCount += msgList.size();
						}

						if (msgList == null || msgList.size() < SYNC_MAX_MESSAGE_COUNT) {
							break;
						}
					}

				}
			}

			logger.debug("client={} siteUserId={} deviceId={} sync group-msg count={}", command.getClientIp(),
					siteUserId, deviceId, syncTotalCount);
			msgFinishToClient(channelSession.getChannel(), siteUserId, deviceId);
		} catch (Exception e) {
			logger.error("sync group message error.command=" + command, e);
		}

		return true;
	}

	private long groupMessageToClient(Channel channel, String userId, List<GroupMessageBean> groupMessageList) {
		long nextPointer = 0;
		ImStcMessageProto.ImStcMessageRequest.Builder requestBuilder = ImStcMessageProto.ImStcMessageRequest
				.newBuilder();
		for (GroupMessageBean bean : groupMessageList) {
			try {
				nextPointer = NumUtils.getMax(nextPointer, bean.getId());

				switch (bean.getMsgType()) {
				case CoreProto.MsgType.GROUP_TEXT_VALUE:
					CoreProto.GroupText groupText = CoreProto.GroupText.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteGroupId(bean.getSiteGroupId())
							.setText(ByteString.copyFromUtf8(bean.getContent())).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer gmsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.GROUP_TEXT).setPointer(bean.getId()).setGroupText(groupText).build();
					requestBuilder.addList(gmsg);
					break;

				case CoreProto.MsgType.GROUP_SECRET_TEXT_VALUE:
					// do nothing
					break;
				case CoreProto.MsgType.GROUP_IMAGE_VALUE:
					CoreProto.GroupImage groupImage = CoreProto.GroupImage.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteGroupId(bean.getSiteGroupId())
							.setImageId(bean.getContent()).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer groupImageMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.GROUP_IMAGE).setPointer(bean.getId()).setGroupImage(groupImage).build();
					requestBuilder.addList(groupImageMsg);
					break;
				case CoreProto.MsgType.GROUP_SECRET_IMAGE_VALUE:
					// do nothing
					break;
				case CoreProto.MsgType.GROUP_VOICE_VALUE:
					CoreProto.GroupVoice groupVoice = CoreProto.GroupVoice.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteGroupId(bean.getSiteGroupId())
							.setVoiceId(bean.getContent()).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer groupVoiceMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.GROUP_VOICE).setPointer(bean.getId()).setGroupVoice(groupVoice).build();
					requestBuilder.addList(groupVoiceMsg);
					break;

				case CoreProto.MsgType.GROUP_SECRET_VOICE_VALUE:
					// do nothing
					break;

				case CoreProto.MsgType.GROUP_NOTICE_VALUE:
					CoreProto.GroupMsgNotice groupNotice = CoreProto.GroupMsgNotice.newBuilder()
							.setMsgId(bean.getMsgId()).setSiteUserId(bean.getSendUserId())
							.setSiteGroupId(bean.getSiteGroupId()).setText(ByteString.copyFromUtf8((bean.getContent())))
							.setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer groupMsgNotice = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setPointer(bean.getId()).setType(MsgType.GROUP_NOTICE).setGroupMsgNotice(groupNotice)
							.build();
					requestBuilder.addList(groupMsgNotice);
					break;
				case CoreProto.MsgType.GROUP_WEB_VALUE:
					WebBean webBean = GsonUtils.fromJson(bean.getContent(), WebBean.class);
					CoreProto.GroupWeb groupWeb = CoreProto.GroupWeb.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteGroupId(bean.getSiteGroupId())
							.setWebCode(webBean.getWebCode()).setHeight(webBean.getHeight())
							.setWidth(webBean.getWidth()).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer groupWebMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setPointer(bean.getId()).setType(MsgType.GROUP_WEB).setGroupWeb(groupWeb).build();
					requestBuilder.addList(groupWebMsg);
					break;
				case CoreProto.MsgType.GROUP_WEB_NOTICE_VALUE:
					CoreProto.GroupWebNotice groupWebNotice = CoreProto.GroupWebNotice.newBuilder()
							.setMsgId(bean.getMsgId()).setSiteUserId(bean.getSendUserId())
							.setSiteGroupId(bean.getSiteGroupId()).setWebCode(bean.getContent())
							.setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer groupWebNoticeMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setPointer(bean.getId()).setType(MsgType.GROUP_WEB_NOTICE)
							.setGroupWebNotice(groupWebNotice).build();
					requestBuilder.addList(groupWebNoticeMsg);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				logger.error(StringHelper.format("sync group message error bean={}", bean), e);
			}
		}

		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		ImStcMessageProto.ImStcMessageRequest request = requestBuilder.build();
		CoreProto.TransportPackageData datas = CoreProto.TransportPackageData.newBuilder().putAllHeader(header)
				.setData(ByteString.copyFrom(request.toByteArray())).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
				.add(datas.toByteArray()));

		return nextPointer;
	}

	private void msgFinishToClient(Channel channel, String siteUserId, String deviceId) {
		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder().putAllHeader(header).build();
		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_FINISH)
				.add(data.toByteArray()));
	}

}
