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
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserGroupDao;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.message.push.WritePackage;
import com.akaxin.site.message.threads.MultiPushThreadExecutor;
import com.akaxin.site.message.utils.SiteConfigHelper;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.google.protobuf.ByteString;

public class GroupPushHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupPushHandler.class);
	private IGroupDao groupDao = new GroupDaoService();

	public Boolean handle(Command command) {
		logger.info("开始发送群PUSH command={}", command.toString());
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
					String siteUserId = command.getSiteUserId();
					String siteFromId = siteUserId;
					String siteGroupId = command.getSiteGroupId();
					logger.info("group push command={}", command.toString());

					GroupProfileBean groupBean = ImUserGroupDao.getInstance().getSimpleGroupProfile(siteGroupId);

					if (groupBean == null) {
						return;
					}

					List<String> groupMembers = groupDao.getGroupMembersId(siteGroupId);
					for (String memberUserId : groupMembers) {

						if (StringUtils.isNotBlank(memberUserId) && !memberUserId.equals(siteUserId)) {

							// 一、用户是否对站点消息免打扰
							// 二、用户是否对该群消息免打扰
							if (ImUserProfileDao.getInstance().isMute(memberUserId)
									|| ImUserGroupDao.getInstance().isMesageMute(memberUserId, siteGroupId)) {
								continue;
							}

							String globalUserId = ImUserProfileDao.getInstance().getGlobalUserId(memberUserId);
							logger.info("push from groupid={} to siteUserId={} globalUserId={}.", siteGroupId,
									memberUserId, globalUserId);

							ApiPushNotificationProto.ApiPushNotificationRequest.Builder requestBuilder = ApiPushNotificationProto.ApiPushNotificationRequest
									.newBuilder();
							requestBuilder.setPushType(request.getType());
							PushProto.Notification.Builder notification = PushProto.Notification.newBuilder();

							notification.setUserId(globalUserId);
							// notification.setPushBadge(1);
							String siteName = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_NAME);
							if (StringUtils.isNotBlank(siteName)) {
								notification.setPushTitle(siteName);
							}
							String address = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_ADDRESS);
							String port = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_PORT);
							notification.setSiteServer(address + ":" + port);
							notification.setPushFromId(siteGroupId);
							// 条件1:站点是否支持push展示消息内容
							// 条件2:站点只支持文本消息展示消息内容
							if (ConfigProto.PushClientStatus.PUSH_DISPLAY_TEXT == pcs) {
								if (CoreProto.MsgType.GROUP_TEXT == request.getType()) {
									ByteString byteStr = request.getGroupText().getText();
									notification.setPushAlert(byteStr.toString(Charset.forName("UTF-8")));
								}
								if (StringUtils.isNotEmpty(groupBean.getGroupName())) {
									notification.setPushFromName(groupBean.getGroupName());
								}
							}

							String userToken = ImUserProfileDao.getInstance().getUserToken(memberUserId);
							if (StringUtils.isNotBlank(userToken)) {
								notification.setUserToken(userToken);
								requestBuilder.setNotification(notification.build());
								logger.info("Akaxin Push: {}", requestBuilder.toString());

								WritePackage.getInstance().asyncWrite(CommandConst.API_PUSH_NOTIFICATION,
										requestBuilder.build().toByteArray());
							}

						}
					}

				} catch (Exception e) {
					logger.error("group push error.", e);
				}
			}
		});

		return false;
	}

}
