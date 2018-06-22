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
package com.akaxin.site.business.impl.notice;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.business.constant.NoticeText;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.message.api.IMessageService;
import com.akaxin.site.message.service.ImMessageService;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.google.protobuf.ByteString;

/**
 * <pre>
 * 主要发送群主相关的消息通知 
 * 	1.新用户加入了群聊消息提醒
 * 	2.消息通知，以及系统通知
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-02 10:23:00
 */
public class GroupNotice {
	private static final Logger logger = LoggerFactory.getLogger(GroupNotice.class);

	private IMessageService imService = new ImMessageService();

	/**
	 * 新用户加入了群聊 <br>
	 * eg：群聊天界面中，<张三 加入了群聊天>
	 */
	public void addGroupMemberNotice(String siteUserId, String groupId, List<String> userIdList) {
		logger.info("add group member notice siteUserId={} groupId={} userList={}", siteUserId, groupId, userIdList);

		if (StringUtils.isEmpty(groupId) || userIdList == null || userIdList.size() == 0) {
			return;
		}

		StringBuilder noticeText = new StringBuilder();
		try {
			if (StringUtils.isNotEmpty(siteUserId)) {
				// 移除群主,群创建者
				userIdList.remove(siteUserId);
				// 查询群主信息
				SimpleUserBean bean = UserProfileDao.getInstance().getSimpleProfileById(siteUserId);
				if (bean != null && StringUtils.isNotEmpty(bean.getUserName())) {
					noticeText.append(bean.getUserName());
					noticeText.append(" 邀请了 ");
				}
			}

			int num = 0;
			for (String userId : userIdList) {
				if (StringUtils.isEmpty(siteUserId)) {
					siteUserId = userId;
				}
				SimpleUserBean memberBean = UserProfileDao.getInstance().getSimpleProfileById(userId);
				if (memberBean != null && StringUtils.isNotEmpty(memberBean.getUserName())) {
					noticeText.append(memberBean.getUserName());
					if (num++ < (userIdList.size() - 1)) {
						noticeText.append(",");
					}
				}
			}

			if (noticeText.length() == 0) {
				noticeText.append("新人");
			}

			noticeText.append(NoticeText.USER_ADD_GROUP);
			this.groupMsgNotice(siteUserId, groupId, noticeText.toString());
		} catch (Exception e) {
			logger.error("new group member notice error. notice=" + noticeText.toString(), e);
		}
	}

	/**
	 * 往群组中发送通知消息<eg:王小王加入了群聊天>
	 * 
	 * @param groupId
	 *            发送通知的群
	 * @param siteUserId
	 *            备用字段，兼容官方号推送通知消息，发送方ID
	 * 
	 */
	private void groupMsgNotice(String siteUserId, String siteGroupId, String noticeText) {
		CoreProto.GroupMsgNotice groupMsgNotice = CoreProto.GroupMsgNotice.newBuilder()
				.setMsgId(buildGroupMsgId(siteUserId)).setSiteUserId(siteUserId).setSiteGroupId(siteGroupId)
				.setText(ByteString.copyFromUtf8(noticeText)).setTime(System.currentTimeMillis()).build();
		ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
				.setType(CoreProto.MsgType.GROUP_NOTICE).setGroupMsgNotice(groupMsgNotice).build();
		Command command = new Command();
		command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
		command.setSiteUserId(siteUserId);
		command.setSiteGroupId(siteGroupId);
		command.setParams(request.toByteArray());
		logger.debug("group msg notice command={}", command.toString());
		imService.execute(command);
	}

	private String buildGroupMsgId(String siteUserid) {
		StringBuilder sb = new StringBuilder("GROUP-");
		if (StringUtils.isNotEmpty(siteUserid)) {
			int len = siteUserid.length();
			sb.append(siteUserid.substring(0, len >= 8 ? 8 : len));
			sb.append("-");
		}
		sb.append(System.currentTimeMillis());
		return sb.toString();
	}
}
