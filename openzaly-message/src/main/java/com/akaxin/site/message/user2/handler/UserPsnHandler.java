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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.client.ImStcPsnProto;
import com.akaxin.site.message.dao.ImUserSessionDao;

public class UserPsnHandler extends AbstractU2Handler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(UserPsnHandler.class);

	public Boolean handle(Command command) {
		try {
			String siteUserId = command.getSiteUserId();
			String siteFriendId = command.getSiteFriendId();

			// 查找对方的设备信息，发送psh
			List<String> deviceIdList = ImUserSessionDao.getInstance().getSessionDevices(siteFriendId);
			command.setField("deviceIdList", deviceIdList);
			sendPsnToUserDevices(deviceIdList);

			// 如果是代发消息，则需要给发送方发送一个psn
			if (command.isProxy()) {
				List<String> proxyDeviceList = ImUserSessionDao.getInstance()
						.getSessionDevices(command.getProxySiteUserId());
				sendPsnToUserDevices(proxyDeviceList);
			}

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}
		return false;
	}

	private void sendPsnToUserDevices(List<String> deviceIdList) {
		if (deviceIdList == null || deviceIdList.size() == 0) {
			return;
		}
		for (String deviceId : deviceIdList) {
			if (deviceId != null) {
				writePsn(deviceId);
			}
		}
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
