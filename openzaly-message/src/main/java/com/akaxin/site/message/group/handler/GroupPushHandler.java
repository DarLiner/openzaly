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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.service.GroupDaoService;

public class GroupPushHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupPushHandler.class);
	private IGroupDao groupDao = new GroupDaoService();

	public boolean handle(Command command) {
		logger.info("---------group message push---------");
		try {
//			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
//					.parseFrom(command.getParams());
//			String siteUserId = command.getSiteUserId();
//			String siteGroupId = command.getSiteGroupId();
//
//			logger.info("command={}", command.toString());
//
//			List<String> groupMembers = groupDao.getGroupMembersId(siteGroupId);
//			for (String memberUserId : groupMembers) {
//				logger.info("push to siteUserId={}.", memberUserId);
//				if (StringUtils.isNotBlank(memberUserId) && !memberUserId.equals(siteUserId)) {
//
//					ApiPushNotificationProto.ApiPushNotificationRequest.Builder requestBuilder = ApiPushNotificationProto.ApiPushNotificationRequest
//							.newBuilder();
//					requestBuilder.setPushType(request.getType());
//					PushProto.Notification.Builder notification = PushProto.Notification.newBuilder();
//
//					notification.setUserId(memberUserId);
//					notification.setPushBadge(1);
//					String siteName = SiteHelper.getConfig(ConfigProto.ConfigKey.SITE_NAME);
//					if (StringUtils.isNotBlank(siteName)) {
//						notification.setPushTitle(siteName);
//					}
//					String address = SiteHelper.getConfig(ConfigProto.ConfigKey.SITE_ADDRESS);
//					String port = SiteHelper.getConfig(ConfigProto.ConfigKey.SITE_PORT);
//					notification.setSiteServer(address + ":" + port);
//
//					String userToken = ImUserProfileDao.getInstance().getUserToken(memberUserId);
//					if (StringUtils.isNotBlank(userToken)) {
//						notification.setUserToken(userToken);
//						requestBuilder.setNotification(notification.build());
//						logger.info("Akaxin Push: {}", requestBuilder.toString());
//
//						WritePackage.getInstance().asyncWrite(CommandConst.API_PUSH_NOTIFICATION,
//								requestBuilder.build().toByteArray());
//					}
//
//				}
//			}

		} catch (Exception e) {
			logger.error("group push error.", e);
		}
		return false;
	}

}
