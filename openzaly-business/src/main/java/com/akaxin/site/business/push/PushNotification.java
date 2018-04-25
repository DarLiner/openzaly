package com.akaxin.site.business.push;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.akaxin.site.message.dao.ImUserProfileDao;
import com.akaxin.site.message.push.WritePackage;
import com.akaxin.site.message.threads.MultiPushThreadExecutor;
import com.akaxin.site.message.utils.SiteConfigHelper;

public class PushNotification {
	private static final Logger logger = LoggerFactory.getLogger(PushNotification.class);

	public static void sendAddFriend(String siteUserId, String siteFriendId) {
		try {
			String pushContent = PushText.addFriendText(siteUserId);
			String pushGoto = PushText.adFriendGoto(siteFriendId);
			send(siteUserId, siteFriendId, null, pushContent, pushGoto);
		} catch (Exception e) {
			logger.error("add friend push error", e);
		}
	}

	public static void send(String siteUserId, String siteFriendId, String subTitle, String pushContent,
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
					if (StringUtils.isNotEmpty(subTitle)) {
						notification.setPushFromName(subTitle);
					}
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
