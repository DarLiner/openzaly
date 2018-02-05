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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.proto.plugin.HaiFriendApplyProto;
import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.notice.User2Notice;

/**
 * 扩展：用户通讯录，添加用户为好友
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
		logger.info("/hai/friend/apply");
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiFriendApplyProto.HaiFriendApplyRequest request = HaiFriendApplyProto.HaiFriendApplyRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String siteFriendId = request.getSiteFriendId();
			String applyReason = request.getApplyReason();

			logger.info("/hai/friend/apply request={}", request.toString());

			if (StringUtils.isBlank(siteUserId)) {
				errCode = ErrorCode2.ERROR_PARAMETER;
			} else if (siteUserId.equals(siteFriendId)) {
				errCode = ErrorCode2.ERROR2_FRIEND_APPLYSELF;
			} else {
				int applyTimes = UserFriendDao.getInstance().getApplyCount(siteFriendId, siteUserId);
				if (applyTimes >= 5) {
					errCode = ErrorCode2.ERROR2_FRIEND_APPLYCOUNT;
				} else {
					if (UserFriendDao.getInstance().saveFriendApply(siteUserId, siteFriendId, applyReason)) {
						errCode = ErrorCode2.SUCCESS;
					}
				}
			}

			if (ErrorCode2.SUCCESS.equals(errCode)) {
				logger.info("user apply friend notice. to siteUserId={}", siteFriendId);
				new User2Notice().applyFriendNotice(siteFriendId);
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("hai apply friend error.", e);
		}
		logger.info("/hai/friend/apply result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}
}
