package com.akaxin.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akaxin.admin.service.IUserService;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;

@Service("userManageService")
public class UserManageService implements IUserService {

	@Override
	public UserProfileBean getUserProfile(String siteUserId) {
		return UserProfileDao.getInstance().getUserProfileById(siteUserId);
	}

	@Override
	public boolean updateProfile(UserProfileBean userProfileBean) {
		return UserProfileDao.getInstance().updateUserProfile(userProfileBean);
	}

	@Override
	public List<SimpleUserBean> getUserList(int pageNum, int pageSize) {
		return UserProfileDao.getInstance().getUserPageList(pageNum, pageSize);
	}

	@Override
	public boolean sealUpUser(String siteUserId, int status) {
		return UserProfileDao.getInstance().updateUserStatus(siteUserId, status);
	}

}
