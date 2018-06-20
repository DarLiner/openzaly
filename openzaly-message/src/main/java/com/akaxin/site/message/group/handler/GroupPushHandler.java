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

import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushNotificationsProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserGroupDao;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.message.push.PushClient;
import com.akaxin.site.message.threads.MultiPushThreadExecutor;
import com.akaxin.site.message.utils.SiteConfigHelper;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.google.protobuf.ByteString;

/**
 * 群push处理handler
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-20 21:46:15
 */
public class GroupPushHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupPushHandler.class);
	private IGroupDao groupDao = new GroupDaoService();

	public Boolean handle(Command command) {
		// 1.检测当前站点是否开启PUSH开关，开启才支持PUSH功能
		ConfigProto.PushClientStatus pcs = SiteConfigHelper.getPushClientStatus();
		if (ConfigProto.PushClientStatus.PUSH_NO == pcs) {
			logger.warn("push to client error. cause: pushClientStatus={}", ConfigProto.PushClientStatus.PUSH_NO);
			return true;
		}

		// 多线程处理push
		MultiPushThreadExecutor.getExecutor().execute(new Runnable() {

			@Override
			public void run() {
				try {
					ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
							.parseFrom(command.getParams());
					// String siteUserId = command.getSiteUserId();
					String fromSiteUserId = command.isProxy() ? command.getProxySiteUserId() : command.getSiteUserId(); //
					String siteGroupId = command.getSiteGroupId();

					GroupProfileBean groupBean = ImUserGroupDao.getInstance().getSimpleGroupProfile(siteGroupId);
					if (groupBean == null || groupBean.getGroupId() == null) {
						logger.error("send message with push to group={} error", groupBean);
						return;
					}

					String fromGlobalUserId = ImUserProfileDao.getInstance().getGlobalUserId(fromSiteUserId);
					// Push Request
					ApiPushNotificationsProto.ApiPushNotificationsRequest.Builder requestBuilder = ApiPushNotificationsProto.ApiPushNotificationsRequest
							.newBuilder();
					// 1.set pushType
					requestBuilder.setPushTypeValue(request.getType().getNumber());
					// 2.set notification
					PushProto.Notifications.Builder notifications = PushProto.Notifications.newBuilder();
					String siteName = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_NAME);
					if (StringUtils.isNotBlank(siteName)) {
						notifications.setPushTitle(siteName);
					}
					String address = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_ADDRESS);
					String port = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_PORT);
					notifications.setSiteServer(address + ":" + port);
					// 条件1:站点是否支持push展示消息内容
					// 条件2:站点只支持文本消息展示消息内容
					if (ConfigProto.PushClientStatus.PUSH_DISPLAY_TEXT == pcs) {
						if (CoreProto.MsgType.GROUP_TEXT == request.getType()) {
							ByteString byteStr = request.getGroupText().getText();
							notifications.setPushAlert(byteStr.toString(Charset.forName("UTF-8")));
						}
					}
					requestBuilder.setNotifications(notifications.build());
					// 3.set pushFromUser
					PushProto.PushFromUser pushFromUser = PushProto.PushFromUser.newBuilder()
							.setGlobalUserId(fromGlobalUserId).setSiteUserId(fromSiteUserId)
							.setPushFromName(groupBean.getGroupName()).build();
					requestBuilder.setPushFromUser(pushFromUser);

					// 4.set pushToUser
					List<String> groupMembers = groupDao.getGroupMembersId(siteGroupId);
					for (String memberUserId : groupMembers) {

						if (StringUtils.isNotBlank(memberUserId) && !memberUserId.equals(fromSiteUserId)) {
							// 一、用户是否对站点消息免打扰
							// 二、用户是否对该群消息免打扰
							if (ImUserProfileDao.getInstance().isMute(memberUserId)
									|| ImUserGroupDao.getInstance().isMesageMute(memberUserId, siteGroupId)) {
								continue;
							}

							String globalUserId = ImUserProfileDao.getInstance().getGlobalUserId(memberUserId);
							logger.debug("push from groupid={} to siteUserId={} globalUserId={}.", siteGroupId,
									memberUserId, globalUserId);

							PushProto.PushToUser.Builder pushToUser = PushProto.PushToUser.newBuilder();
							pushToUser.setGlobalUserId(globalUserId);

							String userToken = ImUserProfileDao.getInstance().getUserToken(memberUserId);
							if (StringUtils.isNotBlank(userToken)) {
								pushToUser.setUserToken(userToken);
								requestBuilder.addPushToUser(pushToUser.build());
							} else {
								logger.error("siteUserId={} with no userToken", memberUserId);
							}

						}
					}

					logger.debug("client={} siteUserId={} push to groupId={} siteFriend={} content={}",
							command.getClientIp(), command.getSiteUserId(), command.getSiteGroupId(),
							command.getSiteFriendId(), requestBuilder.toString());
					PushClient.asyncWrite(CommandConst.API_PUSH_NOTIFICATIONS, requestBuilder.build().toByteArray());

				} catch (Exception e) {
					LogUtils.requestErrorLog(logger, command, GroupPushHandler.class, e);
				}
			}
		});

		return true;
	}

}
