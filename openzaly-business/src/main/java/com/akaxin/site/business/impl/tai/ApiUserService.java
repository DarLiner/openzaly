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
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.site.ApiUserMuteProto;
import com.akaxin.proto.site.ApiUserProfileProto;
import com.akaxin.proto.site.ApiUserUpdateMuteProto;
import com.akaxin.proto.site.ApiUserUpdateProfileProto;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.bean.UserProfileBean;

/**
 * 用户个人自身资料相关功能
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.25 15:10:36
 */
public class ApiUserService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(ApiUserService.class);

	/**
	 * 获取用户个人资料信息
	 *
	 * 支持使用globalUserid与siteUserId
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			ApiUserProfileProto.ApiUserProfileRequest request = ApiUserProfileProto.ApiUserProfileRequest
					.parseFrom(command.getParams());
			String currentUserId = command.getSiteUserId();
			String siteUserId = request.getSiteUserId();
			LogUtils.apiRequestLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(siteUserId) && siteUserId.equals(currentUserId)) {
				UserProfileBean userBean = UserProfileDao.getInstance().getUserProfileById(siteUserId);

				if (null == userBean) {
					// 直接复用之前的接口了。
					userBean = UserProfileDao.getInstance().getUserProfileByGlobalUserId(siteUserId);
				}

				if (userBean != null && StringUtils.isNotBlank(userBean.getSiteUserId())) {

					UserProto.UserProfile.Builder userProfileBuilder = UserProto.UserProfile.newBuilder();
					userProfileBuilder.setSiteUserId(userBean.getSiteUserId());
					if (userBean.getUserName() != null) {
						userProfileBuilder.setUserName(userBean.getUserName());
					}
					if (userBean.getUserPhoto() != null) {
						userProfileBuilder.setUserPhoto(userBean.getUserPhoto());
					}
					if (userBean.getSelfIntroduce() != null) {
						userProfileBuilder.setSelfIntroduce(userBean.getSelfIntroduce());
					}
					userProfileBuilder.setUserStatusValue(userBean.getUserStatus());

					ApiUserProfileProto.ApiUserProfileResponse response = ApiUserProfileProto.ApiUserProfileResponse
							.newBuilder().setUserProfile(userProfileBuilder.build()).build();
					commandResponse.setParams(response.toByteArray());
					errorCode = ErrorCode2.SUCCESS;
				}
			} else {
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.apiErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 更新用户个人信息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse updateProfile(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiUserUpdateProfileProto.ApiUserUpdateProfileRequest request = ApiUserUpdateProfileProto.ApiUserUpdateProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String userName = request.getUserProfile().getUserName();
			String userPhoto = request.getUserProfile().getUserPhoto();
			String introduce = request.getUserProfile().getSelfIntroduce();
			LogUtils.apiRequestLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(siteUserId, userName, userPhoto)) {

				UserProfileBean userBean = new UserProfileBean();
				userBean.setSiteUserId(siteUserId);
				userBean.setUserName(userName);
				userBean.setUserPhoto(userPhoto);
				userBean.setSelfIntroduce(introduce);

				if (UserProfileDao.getInstance().updateUserProfile(userBean)) {
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR2_USER_UPDATE_PROFILE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.apiErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	public CommandResponse mute(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			String siteUserId = command.getSiteUserId();
			boolean mute = UserProfileDao.getInstance().getUserMute(siteUserId);
			LogUtils.apiRequestLog(logger, command, "");

			ApiUserMuteProto.ApiUserMuteResponse response = ApiUserMuteProto.ApiUserMuteResponse.newBuilder()
					.setMute(mute).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.apiErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	public CommandResponse updateMute(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiUserUpdateMuteProto.ApiUserUpdateMuteRequest request = ApiUserUpdateMuteProto.ApiUserUpdateMuteRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			boolean mute = request.getMute();
			LogUtils.apiRequestLog(logger, command, request.toString());

			if (UserProfileDao.getInstance().updateUserMute(siteUserId, mute)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.apiErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

}
