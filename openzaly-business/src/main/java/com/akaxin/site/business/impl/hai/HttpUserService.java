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
package com.akaxin.site.business.impl.hai;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.plugin.HaiUserListProto;
import com.akaxin.proto.plugin.HaiUserProfileProto;
import com.akaxin.proto.plugin.HaiUserRelationListProto;
import com.akaxin.proto.plugin.HaiUserSealUpProto;
import com.akaxin.proto.plugin.HaiUserUpdateProto;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserProfileBean;

/**
 * 通过Http请求，获取用户相关。
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.28 17:28:31
 */
public class HttpUserService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpUserService.class);

	/**
	 * 查看用户的个人profile
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiUserProfileProto.HaiUserProfileRequest request = HaiUserProfileProto.HaiUserProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotEmpty(siteUserId)) {
				UserProfileBean bean = UserProfileDao.getInstance().getUserProfileById(siteUserId);
				if (bean != null && StringUtils.isNotEmpty(bean.getSiteUserId())) {
					UserProto.UserProfile profile = UserProto.UserProfile.newBuilder()
							.setSiteUserId(bean.getSiteUserId()).setUserName(String.valueOf(bean.getUserName()))
							.setUserPhoto(String.valueOf(bean.getUserPhoto())).setUserStatusValue(bean.getUserStatus())
							.build();
					HaiUserProfileProto.HaiUserProfileResponse response = HaiUserProfileProto.HaiUserProfileResponse
							.newBuilder().setUserProfile(profile).build();
					commandResponse.setParams(response.toByteArray());
					errorCode = ErrorCode2.SUCCESS;
				} else {
					errorCode = ErrorCode2.ERROR2_USER_NOUSER;
				}
			} else {
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 更新用户信息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse update(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiUserUpdateProto.HaiUserUpdateRequest request = HaiUserUpdateProto.HaiUserUpdateRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getUserProfile().getSiteUserId();
			String userName = request.getUserProfile().getUserName();
			String userPhoto = request.getUserProfile().getUserPhoto();
			String userIntro = request.getUserProfile().getSelfIntroduce();
			LogUtils.requestDebugLog(logger, command, request.toString());

			// 过滤参数
			if (StringUtils.isNoneBlank(siteUserId)) {
				UserProfileBean bean = new UserProfileBean();
				bean.setSiteUserId(siteUserId);
				bean.setUserName(userName);
				bean.setUserPhoto(userPhoto);
				bean.setSelfIntroduce(userIntro);
				if (UserProfileDao.getInstance().updateUserProfile(bean)) {
					errCode = ErrorCode2.SUCCESS;
				}
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
	 * <pre>
	 * 		禁封/解禁 用户身份
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse sealUp(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiUserSealUpProto.HaiUserSealUpRequest request = HaiUserSealUpProto.HaiUserSealUpRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			UserProto.UserStatus userStatus = request.getStatus();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(siteUserId)) {
				if (UserProfileDao.getInstance().updateUserStatus(siteUserId, userStatus.getNumber())) {
					errCode = ErrorCode2.SUCCESS;
				}
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
	 * 分页获取用户列表
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiUserListProto.HaiUserListRequest request = HaiUserListProto.HaiUserListRequest
					.parseFrom(command.getParams());
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			LogUtils.requestDebugLog(logger, command, request.toString());

			List<SimpleUserBean> pageList = UserProfileDao.getInstance().getUserPageList(pageNum, pageSize);
			if (pageList != null) {
				HaiUserListProto.HaiUserListResponse.Builder responseBuilder = HaiUserListProto.HaiUserListResponse
						.newBuilder();
				for (SimpleUserBean bean : pageList) {
					UserProto.SimpleUserProfile.Builder userProfileBuilder = UserProto.SimpleUserProfile.newBuilder();
					userProfileBuilder.setSiteUserId(bean.getUserId());
					if (StringUtils.isNotBlank(bean.getUserName())) {
						userProfileBuilder.setUserName(bean.getUserName());
					}
					if (StringUtils.isNotBlank(bean.getUserPhoto())) {
						userProfileBuilder.setUserPhoto(bean.getUserPhoto());
					}
					userProfileBuilder.setUserStatusValue(bean.getUserStatus());

					userProfileBuilder.setUserStatusValue(bean.getUserStatus());
					responseBuilder.addUserProfile(userProfileBuilder.build());
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errorCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 分页获取用户列表，同时附带与当前用户之间关系
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse relationList(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiUserRelationListProto.HaiUserRelationListRequest request = HaiUserRelationListProto.HaiUserRelationListRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			LogUtils.requestDebugLog(logger, command, request.toString());

			List<SimpleUserRelationBean> pageList = UserProfileDao.getInstance().getUserRelationPageList(siteUserId,
					pageNum, pageSize);
			if (pageList != null) {
				HaiUserRelationListProto.HaiUserRelationListResponse.Builder responseBuilder = HaiUserRelationListProto.HaiUserRelationListResponse
						.newBuilder();
				for (SimpleUserRelationBean bean : pageList) {
					UserProto.UserRelationProfile.Builder userProfileBuilder = UserProto.UserRelationProfile
							.newBuilder();
					UserProto.SimpleUserProfile.Builder supBuilder = UserProto.SimpleUserProfile.newBuilder();
					supBuilder.setSiteUserId(bean.getUserId());
					if (StringUtils.isNotBlank(bean.getUserId())) {
						supBuilder.setUserName(String.valueOf(bean.getUserName()));
					}
					if (StringUtils.isNotBlank(bean.getUserPhoto())) {
						supBuilder.setUserPhoto(bean.getUserPhoto());
					}
					supBuilder.setUserStatusValue(bean.getUserStatus());
					userProfileBuilder.setProfile(supBuilder);
					userProfileBuilder.setRelationValue(bean.getRelation());
					responseBuilder.addUserProfile(userProfileBuilder.build());
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errorCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errorCode);
	}
}
