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
package com.akaxin.site.message.notice.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.client.ImStcNoticeProto;
import com.akaxin.site.message.dao.SessionDeviceDao;

/**
 * 在线给客户端下发通知命令
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-03 19:02:12
 */
public class NoticeHandler extends AbstractNoticeHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(NoticeHandler.class);

	public Boolean handle(Command command) {
		try {
			ImStcNoticeProto.ImStcNoticeRequest request = ImStcNoticeProto.ImStcNoticeRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String siteFriendId = command.getSiteFriendId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			List<String> deviceList = SessionDeviceDao.getInstance().getSessionDevices(siteFriendId);
			for (String deviceId : deviceList) {
				CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
						.setAction(CommandConst.IM_NOTICE);
				commandResponse.setParams(request.toByteArray());
				commandResponse.setErrCode2(ErrorCode2.SUCCESS);
				ChannelWriter.writeByDeviceId(deviceId, commandResponse);
				logger.debug("siteUserId={} apply friend to siteFriendId={} deviceId={}", siteUserId, siteFriendId,
						deviceId);
			}

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		return false;
	}

}
