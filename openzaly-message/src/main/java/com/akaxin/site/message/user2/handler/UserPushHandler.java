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
package com.akaxin.site.message.user2.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.message.push.WritePackage;
import com.akaxin.site.message.threads.MultiPushThreadExecutor;
import com.akaxin.site.message.utils.SiteConfigHelper;
import com.google.protobuf.InvalidProtocolBufferException;

public class UserPushHandler extends AbstractUserHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(UserPushHandler.class);

	public boolean handle(Command command) {
		// 多线程处理push
		MultiPushThreadExecutor.getExecutor().execute(new Runnable() {
			
			@Override
			public void run() {

				logger.info("-----------u2 message push----------");
				try {
					ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
							.parseFrom(command.getParams());
					String siteFriendId = command.getSiteFriendId();// 这里是用户生成的站点ID
					String globalUserId = ImUserProfileDao.getInstance().getGlobalUserId(siteFriendId);
					logger.info("globalUserId={} command={}", globalUserId, command.toString());

					ApiPushNotificationProto.ApiPushNotificationRequest.Builder requestBuilder = ApiPushNotificationProto.ApiPushNotificationRequest
							.newBuilder();
					requestBuilder.setPushType(request.getType());
					PushProto.Notification.Builder notification = PushProto.Notification.newBuilder();
					notification.setUserId(globalUserId);
					notification.setPushBadge(1);
					String siteName = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_NAME);
					if (StringUtils.isNotBlank(siteName)) {
						notification.setPushTitle(siteName);
					}
					String address = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_ADDRESS);
					String port = SiteConfigHelper.getConfig(ConfigProto.ConfigKey.SITE_PORT);
					notification.setSiteServer(address + ":" + port);

					String userToken = ImUserProfileDao.getInstance().getUserToken(siteFriendId);
					if (StringUtils.isNotBlank(userToken)) {
						notification.setUserToken(userToken);
						requestBuilder.setNotification(notification.build());
						logger.info("Akaxin Push: {}", requestBuilder.toString());

						WritePackage.getInstance().asyncWrite(CommandConst.API_PUSH_NOTIFICATION,
								requestBuilder.build().toByteArray());
					} else {
						logger.warn("Akaxin Push error,usertoken={}", userToken);
					}
				} catch (InvalidProtocolBufferException e) {
					logger.error("u2 message push error", e);
				}
			}
		});
		return true;
	}

}
