package com.akaxin.site.web.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;

@Service("webChatService")
public class WebChatService {

	public List<SimpleUserBean> getChatList(String siteUserId) {
		// return UserFriendDao.getInstance().getUserFriends(siteUserId);
		return null;
	}

	public List<SimpleUserBean> getUserFriendList(String siteUserId) {
		return UserFriendDao.getInstance().getUserFriends(siteUserId);
	}

	public List<SimpleGroupBean> getUserGroupList(String siteUserId) {
		return UserGroupDao.getInstance().getUserGroups(siteUserId);
	}
}
