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
import com.akaxin.proto.site.ImSyncFinishProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.service.MessageDaoService;

public class SyncFinishHandler extends AbstractSyncHandler<Command> {
	private static Logger logger = LoggerFactory.getLogger(SyncFinishHandler.class);
	private IMessageDao syncDao = new MessageDaoService();

	public boolean handle(Command command) {
		logger.info("this is Im.SyncFinish Handler");
		try {
			ImSyncFinishProto.ImSyncFinishRequest request = ImSyncFinishProto.ImSyncFinishRequest
					.parseFrom(command.getParams());

			String deviceId = command.getDeviceId();
			String siteUserId = command.getSiteUserId();
			long u2Pointer = request.getU2Pointer();
			Map<String, Long> groupPointer = request.getGroupsPointerMap();

			logger.info("siteUserId={} U2Message syncFinish u2Pointer={}", siteUserId, u2Pointer);

			syncDao.updateU2Pointer(siteUserId, deviceId, u2Pointer);

			logger.info("siteUserId={} GroupMessage syncFinish groupPointer={}", siteUserId, groupPointer);

			for (Map.Entry<String, Long> gidEntry : groupPointer.entrySet()) {
				syncDao.updateGroupPointer(gidEntry.getKey(), siteUserId, deviceId, gidEntry.getValue());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
