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
import com.akaxin.common.exceptions.ZalyException2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.plugin.HaiFriendAddProto;
import com.akaxin.proto.plugin.HaiFriendApplyProto;
import com.akaxin.proto.plugin.HaiFriendRelationsProto.HaiFriendRelationsRequest;
import com.akaxin.proto.plugin.HaiFriendRelationsProto.HaiFriendRelationsResponse;
import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.notice.User2Notice;
import com.akaxin.site.storage.bean.SimpleUserBean;

/**
 * <pre>
 * 	好友相关的扩展功能实现
 * 		hai/friend/add
 * 		hai/friend/apply
 * 		hai/friend/relations
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-10 18:59:10
 */
public class HttpFriendService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpFriendService.class);

	/**
	 * 提供扩展服务中添加好友的功能
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse apply(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiFriendApplyProto.HaiFriendApplyRequest request = HaiFriendApplyProto.HaiFriendApplyRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			String siteFriendId = request.getFriendSiteUserId();
			String applyReason = request.getApplyReason();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, siteFriendId, applyReason)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			if (siteUserId.equals(siteFriendId)) {
				throw new ZalyException2(ErrorCode2.ERROR2_FRIEND_APPLYSELF);
			}

			// 先检测一次二者之间关系
			if (UserFriendDao.getInstance().isFriend(siteUserId, siteFriendId)) {
				throw new ZalyException2(ErrorCode2.ERROR2_FRIEND_IS);
			}

			int applyTimes = UserFriendDao.getInstance().getApplyCount(siteFriendId, siteUserId);
			if (applyTimes >= 5) {
				throw new ZalyException2(ErrorCode2.ERROR2_FRIEND_APPLYCOUNT);
			}

			if (UserFriendDao.getInstance().saveFriendApply(siteUserId, siteFriendId, applyReason)) {
				errCode = ErrorCode2.SUCCESS;
				new User2Notice().applyFriendNotice(siteUserId, siteFriendId);
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

	/**
	 * 执行添加好友功能
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse add(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;

		try {
			HaiFriendAddProto.HaiFriendAddRequest request = HaiFriendAddProto.HaiFriendAddRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			String siteFriendId = request.getFriendSiteUserId();

			if (StringUtils.isAnyEmpty(siteUserId, siteFriendId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			if (siteUserId.equals(siteFriendId)) {
				throw new ZalyException2(ErrorCode2.ERROR2_FRIEND_APPLYSELF);
			}

			// 同意添加好友
			if (UserFriendDao.getInstance().agreeApply(siteUserId, siteFriendId, true)) {
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

	/**
	 * 批量获取用户关系列表
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse relations(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;

		try {
			HaiFriendRelationsRequest request = HaiFriendRelationsRequest.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			List<String> userIdList = request.getTargetSiteUserIdList();

			if (StringUtils.isEmpty(siteUserId) || userIdList == null) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			HaiFriendRelationsResponse.Builder resBuilder = HaiFriendRelationsResponse.newBuilder();
			for (String siteFriendId : userIdList) {
				SimpleUserBean userBean = UserProfileDao.getInstance().getSimpleProfileById(siteFriendId);
				if (userBean != null && StringUtils.isNotEmpty(userBean.getUserId())) {
					UserProto.SimpleUserProfile.Builder userProfileBuilder = UserProto.SimpleUserProfile.newBuilder();
					userProfileBuilder.setSiteUserId(userBean.getUserId());
					userProfileBuilder.setUserName(userBean.getUserName());
					userProfileBuilder.setUserPhoto(userBean.getUserPhoto());
					UserProto.UserRelation userRelation = UserFriendDao.getInstance().getUserRelation(siteUserId,
							siteFriendId);
					UserProto.UserRelationProfile relationProfile = UserProto.UserRelationProfile.newBuilder()
							.setProfile(userProfileBuilder.build()).setRelation(userRelation).build();
					resBuilder.addUserProfile(relationProfile);
				}
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
