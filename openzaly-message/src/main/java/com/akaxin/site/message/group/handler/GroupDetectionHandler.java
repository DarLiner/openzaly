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
package com.akaxin.site.message.group.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserGroupDao;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleUserBean;

/**
 * <pre>
 * 检测群消息发送，是否满足条件
 * 	1.群成员
 * 	2.用户是否被seal up
 * 	3.群是否存在或群状态
 * 	4.设置command中必要参数
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-02-05 11:47:47
 */
public class GroupDetectionHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupDetectionHandler.class);

	public Boolean handle(Command command) {
		try {
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
					.parseFrom(command.getParams());
			int type = request.getType().getNumber();

			String siteUserId = null;
			String siteGroupId = null;
			String gmsgId = null;
			switch (type) {
			case CoreProto.MsgType.GROUP_TEXT_VALUE:
				siteUserId = request.getGroupText().getSiteUserId();
				gmsgId = request.getGroupText().getMsgId();
				siteGroupId = request.getGroupText().getSiteGroupId();
			case CoreProto.MsgType.GROUP_SECRET_TEXT_VALUE:
				break;
			case CoreProto.MsgType.GROUP_IMAGE_VALUE:
				siteUserId = request.getGroupImage().getSiteUserId();
				gmsgId = request.getGroupImage().getMsgId();
				siteGroupId = request.getGroupImage().getSiteGroupId();
				break;
			case CoreProto.MsgType.GROUP_SECRET_IMAGE_VALUE:
				break;
			case CoreProto.MsgType.GROUP_VOICE_VALUE:
				siteUserId = request.getGroupVoice().getSiteUserId();
				gmsgId = request.getGroupVoice().getMsgId();
				siteGroupId = request.getGroupVoice().getSiteGroupId();
				break;
			case CoreProto.MsgType.GROUP_SECRET_VOICE_VALUE:
				break;
			case CoreProto.MsgType.GROUP_NOTICE_VALUE:
				siteGroupId = request.getGroupMsgNotice().getSiteGroupId();
				return true;
			default:
				break;
			}
			// 群消息设置siteGroupId
			command.setSiteGroupId(siteGroupId);
			command.setMsgType(type);

			if (StringUtils.isAnyEmpty(command.getSiteUserId(), command.getSiteGroupId())) {
				return false;
			}

			if (check(siteUserId, siteGroupId)) {
				return true;
			} else {
				logger.warn("client={} siteUserId={} is not group={} member", command.getClientIp(), siteUserId,
						siteGroupId);
				int statusValue = -2;
				msgStatusResponse(command, gmsgId, System.currentTimeMillis(), statusValue);
			}

		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, GroupDetectionHandler.class, e);
		}

		return false;
	}

	private boolean check(String siteUserId, String siteGroupId) {
		return checkUser(siteUserId) && checkGroupStatus(siteGroupId) && isGroupMember(siteUserId, siteGroupId);
	}

	// 1.检测用户状态是否正常（被封禁用户）
	private boolean checkUser(String siteUserId) {
		// 检测发送者的状态
		SimpleUserBean userBean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteUserId);
		if (userBean != null) {
			if (userBean.getUserStatus() != UserProto.UserStatus.NORMAL_VALUE) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	// 2.检测群状态，是否为被删除群
	private boolean checkGroupStatus(String groupId) {
		try {
			GroupProfileBean bean = ImUserGroupDao.getInstance().getGroupProfile(groupId);
			if (bean != null && StringUtils.isNotBlank(bean.getGroupId())) {
				if (bean.getGroupStatus() != 0) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("check group status error", e);
		}
		return false;
	}

	// 3.检测是否为群成员，可以发送群消息
	private boolean isGroupMember(String siteUserId, String groupId) {
		return ImUserGroupDao.getInstance().isGroupMember(siteUserId, groupId);
	}
}
