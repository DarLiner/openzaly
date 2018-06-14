package com.akaxin.site.business.push;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.common.utils.StringHelper;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.SimpleUserBean;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-25 17:41:20
 */
public class PushText {
	private static String ADD_FRIEND_TEXT = "{} 申请添加你为好友";
	private static String AGGREE_FRIEND_TEXT = "{} 同意了你的好友申请";
	private static String DEFAULT_ADD_FRIEND_TEXT = "你收到一条好友申请";

	public static String applyFriendText(String siteUserId) {
		SimpleUserBean bean = UserProfileDao.getInstance().getSimpleProfileById(siteUserId);
		if (bean != null && StringUtils.isNotEmpty(bean.getUserName())) {
			return StringHelper.format(ADD_FRIEND_TEXT, bean.getUserName());
		}
		return DEFAULT_ADD_FRIEND_TEXT;
	}

	public static String applyFriendGoto(String siteUserId) {
		String siteAddress = SiteConfig.getSiteAddress();
		return StringHelper.format("zaly://{}/goto?page=friend_apply", siteAddress);
	}

	// 同意好友添加push文案
	public static String agreeFriendText(String siteUserId) {
		SimpleUserBean bean = UserProfileDao.getInstance().getSimpleProfileById(siteUserId);
		if (bean != null && StringUtils.isNotEmpty(bean.getUserName())) {
			return StringHelper.format(AGGREE_FRIEND_TEXT, bean.getUserName());
		}
		return DEFAULT_ADD_FRIEND_TEXT;
	}

	public static String messageGoto(String siteUserId) {
		String siteAddress = SiteConfig.getSiteAddress();
		return StringHelper.format("zaly://{}/goto?page=u2_msg&site_user_id={}", siteAddress, siteUserId);
	}

}
