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
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.akaxin.proto.plugin.HaiPushNoticesProto.HaiPushNoticesRequest;
import com.akaxin.site.business.dao.SiteUserDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.message.push.WritePackage;
import com.akaxin.site.message.threads.MultiPushThreadExecutor;
import com.akaxin.site.message.utils.SiteConfigHelper;

/**
 * 扩展使用的PUSH服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-14 12:03:55
 */
public class HttpPushService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpPushService.class);

	/**
	 * 向站点所有人推送PUSH通知消息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse notices(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiPushNoticesRequest request = HaiPushNoticesRequest.parseFrom(command.getParams());
			String pushGoto = request.getPushGoto();
			String pushTitle = request.getSubtitle();
			String pushContent = request.getContent();
			String siteUserId = command.getSiteUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			int pageNum = 1;
			int pageSize = 200;

			while (true) {
				List<String> userList = SiteUserDao.getInstance().getSiteUsersByPage(pageNum, pageSize);

				if (userList != null) {
					for (String userId : userList) {
						pushNotification(siteUserId, userId, pushTitle, pushContent, pushGoto);
					}
				}

				if (userList == null || userList.size() < pageSize) {
					break;
				}
			}

			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	private void pushNotification(String siteUserId, String siteFriendId, String subTitle, String pushContent,
			String pushGoto) {
		MultiPushThreadExecutor.getExecutor().execute(new Runnable() {

			@Override
			public void run() {
				try {
					String globalUserId = ImUserProfileDao.getInstance().getGlobalUserId(siteFriendId);

					// 一、用户对站点是否消息免打扰
					if (ImUserProfileDao.getInstance().isMute(siteFriendId)) {
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
					notification.setPushFromId(siteUserId);
					notification.setPushFromName(subTitle);
					notification.setPushAlert(pushContent);
					notification.setPushGoto(pushGoto);

					String userToken = ImUserProfileDao.getInstance().getUserToken(siteFriendId);
					if (StringUtils.isNotBlank(userToken)) {
						notification.setUserToken(userToken);
						requestBuilder.setNotification(notification.build());
						requestBuilder.setPushType(PushProto.PushType.PUSH_NOTICE);
						WritePackage.getInstance().asyncWrite(CommandConst.API_PUSH_NOTIFICATION,
								requestBuilder.build().toByteArray());
					}
				} catch (Exception e) {
					logger.error(StringHelper.format("siteUserId={} siteFriendId={} subtitle={} content={}", siteUserId,
							siteFriendId, subTitle, pushContent), e);
				}
			}
		});
	}
}
