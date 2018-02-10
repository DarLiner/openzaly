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
package com.akaxin.site.business.impl.tai;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.DeviceProto;
import com.akaxin.proto.site.ApiSecretChatApplyU2Proto;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.api.IUserDeviceDao;
import com.akaxin.site.storage.bean.UserDeviceBean;
import com.akaxin.site.storage.service.DeviceDaoService;

/**
 * 用户开启二人／群聊 绝密聊天
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.20
 */
public class ApiSecretChatService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(ApiSecretChatService.class);
	private IUserDeviceDao userDeviceDao = new DeviceDaoService();

	/**
	 * 申请二人密聊
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse applyU2(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiSecretChatApplyU2Proto.ApiSecretChatApplyU2Request request = ApiSecretChatApplyU2Proto.ApiSecretChatApplyU2Request
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String siteFriendId = request.getSiteFriendId();
			logger.info("api.secretChat.applyU2 cmd={} request={}", command.toString(), request.toString());

			if (StringUtils.isNoneBlank(siteUserId, siteFriendId) && !siteUserId.equals(siteFriendId)) {
				ConfigProto.U2EncryptionStatus status = SiteConfig.getU2EncryStatus();
				logger.info("siteUserId={} apply encryption chat to siteFriendId={} status={}", siteUserId,
						siteFriendId, status);
				if (ConfigProto.U2EncryptionStatus.U2_OPEN == status) {
					UserDeviceBean deviceBean = userDeviceDao.getLatestDevice(siteFriendId);
					logger.info("开始获取好友的默认设备信息 get user:{} deviceInfo:{}", siteFriendId, deviceBean.toString());

					DeviceProto.SimpleDeviceProfile deviceProfile = DeviceProto.SimpleDeviceProfile.newBuilder()
							.setDeviceId(deviceBean.getDeviceId())
							.setDeviceName(String.valueOf(deviceBean.getDeviceName()))
							.setUserDevicePubk(deviceBean.getUserDevicePubk())
							.setLastLoginTime(deviceBean.getActiveTime()).build();

					ApiSecretChatApplyU2Proto.ApiSecretChatApplyU2Response response = ApiSecretChatApplyU2Proto.ApiSecretChatApplyU2Response
							.newBuilder().setDeviceProfile(deviceProfile).build();
					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR2_SECRETCHAT_CLOSE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.secretChat.applyU2 exception", e);
		}
		logger.info("api.secretChat.applyU2 result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

}
