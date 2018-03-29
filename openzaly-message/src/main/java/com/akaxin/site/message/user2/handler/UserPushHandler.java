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

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.dao.ImUserFriendDao;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.message.push.WritePackage;
import com.akaxin.site.message.threads.MultiPushThreadExecutor;
import com.akaxin.site.message.utils.SiteConfigHelper;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.google.protobuf.ByteString;

public class UserPushHandler extends AbstractU2Handler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(UserPushHandler.class);

	public Boolean handle(Command command) {
		// 1.判断站点是否开启PUSH发送功能
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
					String siteUserId = command.getSiteUserId();// 发送者
					String siteFromId = siteUserId; // 为什么这样写了，保持读者的阅读性
					String siteFriendId = command.getSiteFriendId();// 接受者 这里是用户生成的站点ID
					String globalUserId = ImUserProfileDao.getInstance().getGlobalUserId(siteFriendId);
					LogUtils.requestDebugLog(logger, command, request.toString());

					// 一、用户对站点是否消息免打扰
					// 二、用户对该好友是否消息免打扰
					if (ImUserProfileDao.getInstance().isMute(siteFriendId)
							|| ImUserFriendDao.getInstance().isMesageMute(siteFriendId, siteFromId)) {
						return;
					}

					ApiPushNotificationProto.ApiPushNotificationRequest.Builder requestBuilder = ApiPushNotificationProto.ApiPushNotificationRequest
							.newBuilder();
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
					notification.setPushFromId(siteFromId);
					// 条件1:站点是否支持push展示消息内容
					// 条件2:站点只支持文本消息展示消息内容
					if (ConfigProto.PushClientStatus.PUSH_DISPLAY_TEXT == pcs
							&& CoreProto.MsgType.TEXT == request.getType()) {
						ByteString byteStr = request.getText().getText();
						notification.setPushAlert(byteStr.toString(Charset.forName("UTF-8")));
						SimpleUserBean bean = ImUserProfileDao.getInstance().getSimpleUserProfile(siteFromId);
						if (bean != null && StringUtils.isNotEmpty(bean.getUserName())) {
							notification.setPushFromName(bean.getUserName());
						}
					}
					String userToken = ImUserProfileDao.getInstance().getUserToken(siteFriendId);
					if (StringUtils.isNotBlank(userToken)) {
						notification.setUserToken(userToken);
						requestBuilder.setNotification(notification.build());
						requestBuilder.setPushType(request.getType());

						WritePackage.getInstance().asyncWrite(CommandConst.API_PUSH_NOTIFICATION,
								requestBuilder.build().toByteArray());
						logger.debug("client={} siteUserId={} push to siteFriend={} content={}", command.getClientIp(),
								command.getSiteUserId(), command.getSiteFriendId(), requestBuilder.toString());
					} else {
						logger.warn("client={} siteUserId={} push to siteFriend={} error as userToken={}",
								command.getClientIp(), command.getSiteUserId(), command.getSiteFriendId(), userToken);
					}
				} catch (Exception e) {
					LogUtils.requestErrorLog(logger, command, UserPushHandler.class, e);
				}
			}
		});
		return true;
	}

}
