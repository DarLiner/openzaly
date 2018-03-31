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

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.site.ImSyncFinishProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.service.MessageDaoService;

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

			// 如果客户端上传的游标大于数据库里最大消息的游标，说明客户端上传的游标错误
			long maxU2MessageId = syncDao.queryMaxU2MessageId(siteUserId);
			long maxU2Pointer = syncDao.queryMaxU2Pointer(siteUserId);
			if (u2Pointer > maxU2MessageId) {
				u2Pointer = maxU2MessageId;
			}
			if (u2Pointer < maxU2Pointer) {
				u2Pointer = maxU2Pointer;
			}

			syncDao.updateU2Pointer(siteUserId, deviceId, u2Pointer);

			logger.debug("siteUserId={} GroupMessage syncFinish groupPointer={}", siteUserId, groupPointer);

			for (Map.Entry<String, Long> gidEntry : groupPointer.entrySet()) {
				String siteGroupId = gidEntry.getKey();
				long groupFinishPointer = gidEntry.getValue();

				long maxGroupMsgPointer = syncDao.queryMaxGroupPointer(siteGroupId);
				long maxUserGroupPointer = syncDao.queryMaxUserGroupPointer(siteGroupId, siteUserId);

				if (groupFinishPointer > maxGroupMsgPointer) {
					groupFinishPointer = maxGroupMsgPointer;
				}

				if (groupFinishPointer < maxUserGroupPointer) {
					groupFinishPointer = maxUserGroupPointer;
				}

				syncDao.updateGroupPointer(siteGroupId, siteUserId, deviceId, groupFinishPointer);
			}
			return true;// 执行成功
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, e);
		}
		return false;
	}

}
