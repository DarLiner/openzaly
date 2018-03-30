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

import java.util.Base64;
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
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.client.ImStcMessageProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImSyncMessageProto;
import com.akaxin.site.message.utils.NumUtils;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;

/**
 * 用户同步个人消息处理类
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-02-08 17:08:47
 */
public class SyncU2MessageHandler extends AbstractSyncHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(SyncU2MessageHandler.class);
	private IMessageDao syncDao = new MessageDaoService();
	private static final int SYNC_MAX_MESSAGE_COUNT = 100;

	public Boolean handle(Command command) {
		ChannelSession channelSession = command.getChannelSession();
		try {
			ImSyncMessageProto.ImSyncMessageRequest syncRequest = ImSyncMessageProto.ImSyncMessageRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String deviceId = command.getDeviceId();
			LogUtils.requestDebugLog(logger, command, syncRequest.toString());

			long clientU2Pointer = syncRequest.getU2Pointer();
			long u2Pointer = syncDao.queryU2Pointer(siteUserId, deviceId);
			long startPointer = NumUtils.getMax(clientU2Pointer, u2Pointer);
			int syncTotalCount = 0;

			while (true) {
				// 批量一次查询100条U2消息
				List<U2MessageBean> u2MessageList = syncDao.queryU2Message(siteUserId, deviceId, startPointer,
						SYNC_MAX_MESSAGE_COUNT);
				// 有二人消息才会发送给客户端，同时返回最大的游标
				if (u2MessageList != null && u2MessageList.size() > 0) {
					startPointer = u2MessageToClient(channelSession.getChannel(), u2MessageList);
					syncTotalCount += u2MessageList.size();
				}
				// 判断跳出循环的条件
				if (u2MessageList == null || u2MessageList.size() < SYNC_MAX_MESSAGE_COUNT) {
					break;
				}
			}

			logger.debug("client={} siteUserId={} deviceId={} sync u2-msg from pointer={} count={}.",
					command.getClientIp(), siteUserId, deviceId, startPointer, syncTotalCount);
		} catch (Exception e) {
			logger.error("sync u2 message error", e);
		}

		return true;
	}

	private long u2MessageToClient(Channel channel, List<U2MessageBean> u2MessageList) {
		long nextPointer = 0;
		ImStcMessageProto.ImStcMessageRequest.Builder requestBuilder = ImStcMessageProto.ImStcMessageRequest
				.newBuilder();

		for (U2MessageBean bean : u2MessageList) {
			try {
				nextPointer = NumUtils.getMax(nextPointer, bean.getId());

				switch (bean.getMsgType()) {
				case CoreProto.MsgType.TEXT_VALUE:
					CoreProto.MsgText MsgText = CoreProto.MsgText.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteFriendId(bean.getSiteUserId())
							.setText(ByteString.copyFromUtf8(bean.getContent())).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer textMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.TEXT).setPointer(bean.getId()).setText(MsgText).build();
					// logger.info("[Syncing U2] text message OK. bean={}", u2Bean);
					requestBuilder.addList(textMsg);
					break;
				case CoreProto.MsgType.SECRET_TEXT_VALUE:
					byte[] secretTexgt = Base64.getDecoder().decode(bean.getContent());
					CoreProto.MsgSecretText secretText = CoreProto.MsgSecretText.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteFriendId(bean.getSiteUserId())
							.setText(ByteString.copyFrom(secretTexgt))
							.setSiteDeviceId(String.valueOf(bean.getDeviceId())).setTsKey(bean.getTsKey())
							.setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer secretTextMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.SECRET_TEXT).setPointer(bean.getId()).setSecretText(secretText).build();
					// logger.info("[Syncing U2] secret text message OK. bean={}",
					// u2Bean);
					requestBuilder.addList(secretTextMsg);
					break;
				case CoreProto.MsgType.IMAGE_VALUE:
					CoreProto.MsgImage msgImage = CoreProto.MsgImage.newBuilder().setImageId(bean.getContent())
							.setMsgId(bean.getMsgId()).setSiteUserId(bean.getSendUserId())
							.setSiteFriendId(bean.getSiteUserId()).setTime(bean.getMsgTime())
							.setImageId(bean.getContent()).build();
					ImStcMessageProto.MsgWithPointer imageMsgWithPointer = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.IMAGE).setPointer(bean.getId()).setImage(msgImage).build();
					// logger.info("[Syncing U2] image message OK. bean={}", u2Bean);
					requestBuilder.addList(imageMsgWithPointer);
					break;
				case CoreProto.MsgType.SECRET_IMAGE_VALUE:
					CoreProto.MsgSecretImage secretImage = CoreProto.MsgSecretImage.newBuilder()
							.setMsgId(bean.getMsgId()).setSiteUserId(bean.getSendUserId())
							.setSiteFriendId(bean.getSiteUserId()).setImageId(bean.getContent())
							.setSiteDeviceId(String.valueOf(bean.getDeviceId())).setTsKey(bean.getTsKey())
							.setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer secretImageMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.SECRET_IMAGE).setPointer(bean.getId()).setSecretImage(secretImage).build();
					// logger.info("[Syncing U2] secret image message OK. bean={}",
					// u2Bean);
					requestBuilder.addList(secretImageMsg);
					break;
				case CoreProto.MsgType.VOICE_VALUE:
					CoreProto.MsgVoice voice = CoreProto.MsgVoice.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteFriendId(bean.getSiteUserId())
							.setVoiceId(bean.getContent()).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer voiceMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.VOICE).setPointer(bean.getId()).setVoice(voice).build();
					// logger.info("[Syncing U2] voice message OK. bean={0}", u2Bean);
					requestBuilder.addList(voiceMsg);
					break;
				case CoreProto.MsgType.SECRET_VOICE_VALUE:
					CoreProto.MsgSecretVoice secretVoice = CoreProto.MsgSecretVoice.newBuilder()
							.setMsgId(bean.getMsgId()).setSiteUserId(bean.getSendUserId())
							.setSiteFriendId(bean.getSiteUserId()).setVoicdId(bean.getContent())
							.setSiteDeviceId(String.valueOf(bean.getDeviceId())).setTsKey(bean.getTsKey())
							.setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer secretVoiceMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setType(MsgType.SECRET_VOICE).setPointer(bean.getId()).setSecretVoice(secretVoice).build();
					// logger.info("[Syncing U2] secret voice message OK. bean={}",
					// u2Bean);
					requestBuilder.addList(secretVoiceMsg);
					break;
				case CoreProto.MsgType.U2_NOTICE_VALUE:
					CoreProto.U2MsgNotice u2Notice = CoreProto.U2MsgNotice.newBuilder().setMsgId(bean.getMsgId())
							.setSiteUserId(bean.getSendUserId()).setSiteFriendId(bean.getSiteUserId())
							.setText(ByteString.copyFromUtf8(bean.getContent())).setTime(bean.getMsgTime()).build();
					ImStcMessageProto.MsgWithPointer u2NoticeMsg = ImStcMessageProto.MsgWithPointer.newBuilder()
							.setPointer(bean.getId()).setType(MsgType.U2_NOTICE).setU2MsgNotice(u2Notice).build();
					requestBuilder.addList(u2NoticeMsg);
					break;
				default:
					logger.error("Message type error! when sync to client bean={}", bean);
					break;

				}
			} catch (Exception e) {
				logger.error(StringHelper.format("sync u2 message error bean={}", bean), e);
			}

		}

		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(CoreProto.HeaderKey.SITE_SERVER_VERSION_VALUE, CommandConst.SITE_VERSION);
		ImStcMessageProto.ImStcMessageRequest request = requestBuilder.build();
		CoreProto.TransportPackageData data = CoreProto.TransportPackageData.newBuilder().putAllHeader(header)
				.setData(ByteString.copyFrom(request.toByteArray())).build();

		channel.writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION).add(CommandConst.IM_MSG_TOCLIENT)
				.add(data.toByteArray()));
		return nextPointer;
	}

}
