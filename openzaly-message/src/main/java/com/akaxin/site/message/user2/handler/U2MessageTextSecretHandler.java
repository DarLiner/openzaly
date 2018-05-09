/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.message.user2.handler;

import java.util.Base64;
import java.util.UUID;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.proto.client.ImStcPsnProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;
import com.google.protobuf.ByteString;

/**
 * 二人加密文本
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-26 15:09:53
 */
public class U2MessageTextSecretHandler extends AbstractU2Handler<Command> {
    private static final Logger logger = LoggerFactory.getLogger(U2MessageTextSecretHandler.class);
    private IMessageDao messageDao = new MessageDaoService();
    private IMessageDao syncDao = new MessageDaoService();

    public Boolean handle(Command command) {
        try {
            int type = command.getMsgType();

            if (CoreProto.MsgType.SECRET_TEXT_VALUE == type) {
                ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
                        .parseFrom(command.getParams());
                String siteUserId = command.getSiteUserId();
                String siteFriendId = command.getSiteFriendId();
                String msgId = request.getSecretText().getMsgId();
                String tsKey = request.getSecretText().getBase64TsKey();
                String tsDeviceId = request.getSecretText().getToDeviceId();
                ByteString byteStr = request.getSecretText().getText();
                String msgText = Base64.getEncoder().encodeToString(byteStr.toByteArray());
                long msgTime = System.currentTimeMillis();
                U2MessageBean u2Bean = new U2MessageBean();
                u2Bean.setMsgId(msgId);
                u2Bean.setMsgType(type);
                u2Bean.setSendUserId(siteUserId);
                u2Bean.setSiteUserId(siteFriendId);
                u2Bean.setContent(msgText);
                u2Bean.setTsKey(tsKey);
                u2Bean.setDeviceId(tsDeviceId);
                u2Bean.setMsgTime(msgTime);

                LogUtils.requestDebugLog(logger, command, u2Bean.toString());

                boolean success = messageDao.saveU2Message(u2Bean);
                msgStatusResponse(command, msgId, msgTime, success);
//				if (siteFriendId.equals("ed7f3565-4769-450c-b500-27918a8aee2c")) {
//					u2Bean.setMsgId(UUID.randomUUID().toString());
//					u2Bean.setMsgType(4);
//					u2Bean.setSendUserId(siteFriendId);
//					u2Bean.setSiteUserId(siteUserId);
//					u2Bean.setMsgTime(msgTime);
//					LogUtils.requestDebugLog(logger, command, u2Bean.toString());
//					String deviceId = command.getDeviceId();
//					messageDao.saveU2Message(u2Bean);
//					long l = syncDao.queryMaxU2MessageId(siteUserId);
//					syncDao.updateU2Pointer(siteUserId, deviceId, l - 1);
//					CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
//							.setAction(CommandConst.IM_STC_PSN);
//					ImStcPsnProto.ImStcPsnRequest pshRequest = ImStcPsnProto.ImStcPsnRequest.newBuilder().build();
//					commandResponse.setParams(pshRequest.toByteArray());
//					commandResponse.setErrCode2(ErrorCode2.SUCCESS);
//					ChannelWriter.writeByDeviceId(deviceId, commandResponse);
//					command.getChannelSession().setSynFinTime(System.currentTimeMillis());
//				}
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
