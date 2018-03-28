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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.DeviceProto;
import com.akaxin.proto.site.ApiDeviceBoundListProto;
import com.akaxin.proto.site.ApiDeviceListProto;
import com.akaxin.proto.site.ApiDeviceProfileProto;
import com.akaxin.site.business.dao.UserDeviceDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.bean.UserDeviceBean;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.20
 *
 */
public class ApiDeviceService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(ApiDeviceService.class);

	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiDeviceProfileProto.ApiDeviceProfileRequest request = ApiDeviceProfileProto.ApiDeviceProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String deviceId = request.getDeviceId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(siteUserId) && StringUtils.isNotBlank(deviceId)) {
				UserDeviceBean deviceBean = UserDeviceDao.getInstance().getDeviceDetails(siteUserId, deviceId);
				DeviceProto.SimpleDeviceProfile deviceProfile = DeviceProto.SimpleDeviceProfile.newBuilder()
						.setDeviceId(String.valueOf(deviceBean.getDeviceId()))
						.setDeviceName(String.valueOf(deviceBean.getDeviceName()))
						.setLastLoginTime(deviceBean.getLoginTime()).build();
				ApiDeviceProfileProto.ApiDeviceProfileResponse response = ApiDeviceProfileProto.ApiDeviceProfileResponse
						.newBuilder().setDeviceProfile(deviceProfile)
						.setLoginIp(String.valueOf(deviceBean.getDeviceIp()))
						.setLastActiveTime(deviceBean.getActiveTime()).build();
				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiDeviceListProto.DeviceListInfoRequest request = ApiDeviceListProto.DeviceListInfoRequest
					.parseFrom(command.getParams());
			String siteFriendId = request.getSiteUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(siteFriendId)) {
				ApiDeviceListProto.DeviceListInfoResponse.Builder responseBuilder = ApiDeviceListProto.DeviceListInfoResponse
						.newBuilder();
				List<UserDeviceBean> deviceList = UserDeviceDao.getInstance().getActiveDeviceList(siteFriendId);
				for (UserDeviceBean device : deviceList) {
					DeviceProto.SimpleDeviceProfile deviceProfile = DeviceProto.SimpleDeviceProfile.newBuilder()
							.setDeviceId(String.valueOf(device.getDeviceId()))
							.setDeviceName(String.valueOf(device.getDeviceName()))
							.setUserDevicePubk(String.valueOf(device.getUserDevicePubk()))
							// 这里使用活跃时间，上次活跃时间
							.setLastLoginTime(device.getActiveTime()).build();
					responseBuilder.addList(deviceProfile);
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 获取用户在该站点所有关联设备号
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse boundList(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiDeviceBoundListProto.ApiDeviceBoundListRequest request = ApiDeviceBoundListProto.ApiDeviceBoundListRequest
					.parseFrom(command.getParams());
			String currentUserId = command.getSiteUserId();
			String siteUserId = request.getSiteUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(currentUserId) && currentUserId.equals(siteUserId)) {
				ApiDeviceBoundListProto.ApiDeviceBoundListResponse.Builder responseBuilder = ApiDeviceBoundListProto.ApiDeviceBoundListResponse
						.newBuilder();
				List<UserDeviceBean> deviceList = UserDeviceDao.getInstance().getBoundDevices(siteUserId);
				for (UserDeviceBean device : deviceList) {
					DeviceProto.SimpleDeviceProfile deviceProfile = DeviceProto.SimpleDeviceProfile.newBuilder()
							.setDeviceId(String.valueOf(device.getDeviceId()))
							.setDeviceName(String.valueOf(device.getDeviceName()))
							.setLastLoginTime(device.getActiveTime()).build();
					responseBuilder.addList(deviceProfile);
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

}
