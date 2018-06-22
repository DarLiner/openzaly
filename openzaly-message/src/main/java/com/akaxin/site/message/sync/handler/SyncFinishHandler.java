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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.client.ImStcPsnProto;
import com.akaxin.proto.site.ImSyncFinishProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 客户端同步消息完成结束标志
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-21 21:23:21
 */
public class SyncFinishHandler extends AbstractSyncHandler<Command> {
	private static Logger logger = LoggerFactory.getLogger(SyncFinishHandler.class);
	private IMessageDao syncDao = new MessageDaoService();

	public Boolean handle(Command command) {
		try {
			ImSyncFinishProto.ImSyncFinishRequest request = ImSyncFinishProto.ImSyncFinishRequest
					.parseFrom(command.getParams());
			String deviceId = command.getDeviceId();
			String siteUserId = command.getSiteUserId();
			long u2Pointer = request.getU2Pointer();
			Map<String, Long> groupPointer = request.getGroupsPointerMap();
			LogUtils.requestDebugLog(logger, command, request.toString());

			boolean goOnPSN = false;

			long maxU2Pointer = syncDao.queryMaxU2Pointer(siteUserId);// 查从库
			if (u2Pointer < maxU2Pointer) {
				u2Pointer = maxU2Pointer;
			}
			// 如果客户端上传的游标大于数据库里个人消息id的最大值，游标错误，使用消息最大id矫正
			long maxU2MessageId = syncDao.queryMaxU2MessageId(siteUserId);// 查主库
			if (u2Pointer > maxU2MessageId) {
				u2Pointer = maxU2MessageId;
			} else if (u2Pointer < maxU2MessageId) {
				goOnPSN = true;
			}

			syncDao.updateU2Pointer(siteUserId, deviceId, u2Pointer);

			// finish to group
			for (Map.Entry<String, Long> gidEntry : groupPointer.entrySet()) {
				String siteGroupId = gidEntry.getKey();
				long groupFinishPointer = gidEntry.getValue();

				long maxUserGroupPointer = syncDao.queryMaxUserGroupPointer(siteGroupId, siteUserId);// 从库
				if (groupFinishPointer < maxUserGroupPointer) {
					groupFinishPointer = maxUserGroupPointer;
				}

				// 群游标结束值 > 用户群组的最大消息id，说明此值错误，使用最大消息id矫正
				long maxGroupMsgPointer = syncDao.queryMaxGroupPointer(siteGroupId);// 查主库
				if (groupFinishPointer > maxGroupMsgPointer) {
					groupFinishPointer = maxGroupMsgPointer;
				} else if (groupFinishPointer < maxGroupMsgPointer) {
					goOnPSN = true;
				}

				syncDao.updateGroupPointer(siteGroupId, siteUserId, deviceId, groupFinishPointer);
			}

			if (goOnPSN) {
				logger.debug("go on psn dbMaxPointer={} request={}", maxU2MessageId, request.toString());
				writePsn(deviceId);
			}

			return true;// 执行成功
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, e);
		}
		return false;
	}

	private void writePsn(String deviceId) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.IM_STC_PSN);
		ImStcPsnProto.ImStcPsnRequest pshRequest = ImStcPsnProto.ImStcPsnRequest.newBuilder().build();
		commandResponse.setParams(pshRequest.toByteArray());
		commandResponse.setErrCode2(ErrorCode2.SUCCESS);
		ChannelWriter.writeByDeviceId(deviceId, commandResponse);
	}

}
