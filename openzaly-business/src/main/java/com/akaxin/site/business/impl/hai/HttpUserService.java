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
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.exceptions.ZalyException;
import com.akaxin.common.exceptions.ZalyException2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.GroupProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.plugin.HaiUserFriendsProto;
import com.akaxin.proto.plugin.HaiUserGroupsProto;
import com.akaxin.proto.plugin.HaiUserListProto;
import com.akaxin.proto.plugin.HaiUserPhoneProto;
import com.akaxin.proto.plugin.HaiUserProfileProto;
import com.akaxin.proto.plugin.HaiUserUpdateProto;
import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;

/**
 * <pre>
 * 	个人相关的扩展功能实现
 * 		hai/user/profile
 * 		hai/user/update
 * 		hai/user/list
 * 		hai/user/phone
 * 		hai/user/groups
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.28 17:28:31
 */
public class HttpUserService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpUserService.class);

	// 个人profile
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiUserProfileProto.HaiUserProfileRequest request = HaiUserProfileProto.HaiUserProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isEmpty(siteUserId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			UserProfileBean bean = UserProfileDao.getInstance().getUserProfileById(siteUserId);
			if (bean == null || StringUtils.isEmpty(bean.getSiteUserId())) {
				throw new ZalyException2(ErrorCode2.ERROR2_USER_NOUSER);
			}

			UserProto.UserProfile profile = UserProto.UserProfile.newBuilder().setSiteUserId(bean.getSiteUserId())
					.setUserName(String.valueOf(bean.getUserName())).setUserPhoto(String.valueOf(bean.getUserPhoto()))
					.setUserStatusValue(bean.getUserStatus()).build();
			HaiUserProfileProto.HaiUserProfileResponse response = HaiUserProfileProto.HaiUserProfileResponse
					.newBuilder().setUserProfile(profile).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	// 更新个人信息
	public CommandResponse update(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiUserUpdateProto.HaiUserUpdateRequest request = HaiUserUpdateProto.HaiUserUpdateRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getUserProfile().getSiteUserId();
			String userName = request.getUserProfile().getUserName();
			String userPhoto = request.getUserProfile().getUserPhoto();
			String userIntro = request.getUserProfile().getSelfIntroduce();
			LogUtils.requestDebugLog(logger, command, request.toString());

			// 过滤参数
			if (StringUtils.isEmpty(siteUserId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			UserProfileBean bean = new UserProfileBean();
			bean.setSiteUserId(siteUserId);
			bean.setUserName(userName);
			bean.setUserPhoto(userPhoto);
			bean.setSelfIntroduce(userIntro);
			if (UserProfileDao.getInstance().updateUserProfile(bean)) {
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	// 获取站点上的所有用户列表
	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiUserListProto.HaiUserListRequest request = HaiUserListProto.HaiUserListRequest
					.parseFrom(command.getParams());
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (pageSize == 0) {
				pageSize = 100;
			}

			List<SimpleUserBean> userPageList = UserProfileDao.getInstance().getUserPageList(pageNum, pageSize);
			if (userPageList == null) {
				throw new ZalyException2(ErrorCode2.ERROR2_USER_NOLIST);
			}

			HaiUserListProto.HaiUserListResponse.Builder responseBuilder = HaiUserListProto.HaiUserListResponse
					.newBuilder();
			for (SimpleUserBean bean : userPageList) {
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
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	// * 获取用户手机号码
	public CommandResponse phone(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiUserPhoneProto.HaiUserPhoneRequest request = HaiUserPhoneProto.HaiUserPhoneRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();

			if (StringUtils.isAnyEmpty(siteUserId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			UserProfileBean userBean = UserProfileDao.getInstance().getUserProfileById(siteUserId);
			if (userBean == null || StringUtils.isEmpty(userBean.getPhoneId())) {
				throw new ZalyException(ErrorCode2.ERROR2_PHONE_HAVE_NO);
			}

			String phoneIdWithCountryCode = userBean.getPhoneId();
			String[] phondIds = phoneIdWithCountryCode.split("_");

			String phoneId = phoneIdWithCountryCode;
			String countryCode = "+86";
			if (phondIds.length == 2) {
				countryCode = phondIds[0];
				phoneId = phondIds[1];
			}

			HaiUserPhoneProto.HaiUserPhoneResponse response = HaiUserPhoneProto.HaiUserPhoneResponse.newBuilder()
					.setPhoneId(phoneId).setCountryCode(countryCode).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	// friends of user
	public CommandResponse friends(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiUserFriendsProto.HaiUserFriendsRequest request = HaiUserFriendsProto.HaiUserFriendsRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();

			if (StringUtils.isEmpty(siteUserId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			if (pageNum == 0 && pageSize == 0) {
				pageNum = 1;
				pageSize = 100;
			}

			List<SimpleUserBean> friendList = UserFriendDao.getInstance().getUserFriendsByPage(siteUserId, pageNum,
					pageSize);
			if (friendList == null || friendList.isEmpty()) {
				throw new ZalyException2(ErrorCode2.ERROR2_USER_NO_FRIEND);
			}

			HaiUserFriendsProto.HaiUserFriendsResponse.Builder resBuilder = HaiUserFriendsProto.HaiUserFriendsResponse
					.newBuilder();
			// 从第一页开始，才给pageNum
			if (pageSize == 1) {
				int totalNum = UserFriendDao.getInstance().getUserFriendNum(siteUserId);
				resBuilder.setPageTotalNum(totalNum);
			}

			for (SimpleUserBean bean : friendList) {
				UserProto.SimpleUserProfile.Builder supBuilder = UserProto.SimpleUserProfile.newBuilder();
				supBuilder.setSiteUserId(bean.getSiteUserId());
				if (StringUtils.isNotEmpty(bean.getAliasName())) {
					supBuilder.setUserName(bean.getAliasName());
				} else {
					supBuilder.setUserName(bean.getUserName());
				}
				supBuilder.setNickName(bean.getUserName());
				supBuilder.setUserPhoto(bean.getUserPhoto());

				resBuilder.addProfile(supBuilder.build());
			}

			commandResponse.setParams(resBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	// groups of user
	public CommandResponse groups(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiUserGroupsProto.HaiUserGroupsRequest request = HaiUserGroupsProto.HaiUserGroupsRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isEmpty(siteUserId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			if (pageNum == 0 && pageSize == 0) {
				pageNum = 1;
				pageSize = 100;
			}

			//
			List<SimpleGroupBean> groupList = UserGroupDao.getInstance().getUserGroupList(siteUserId, pageNum,
					pageSize);

			if (groupList == null || groupList.isEmpty()) {
				throw new ZalyException2(ErrorCode2.ERROR2_USER_NO_GROUP);
			}

			HaiUserGroupsProto.HaiUserGroupsResponse.Builder resBuilder = HaiUserGroupsProto.HaiUserGroupsResponse
					.newBuilder();

			if (pageNum == 1) {
				int pageTotal = UserGroupDao.getInstance().getUserGroupCount(siteUserId);
				resBuilder.setPageTotalNum(pageTotal);
			}

			for (SimpleGroupBean bean : groupList) {
				GroupProto.GroupProfile.Builder sgpBuilder = GroupProto.GroupProfile.newBuilder();
				sgpBuilder.setId(bean.getGroupId());
				if (StringUtils.isNotEmpty(bean.getGroupName())) {
					sgpBuilder.setName(bean.getGroupName());
				}
				if (StringUtils.isNotEmpty(bean.getGroupPhoto())) {
					sgpBuilder.setIcon(bean.getGroupPhoto());
				}
				resBuilder.addProfile(sgpBuilder.build());
			}

			commandResponse.setParams(resBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}
}
