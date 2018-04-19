package com.akaxin.admin.site.service;

import java.util.List;

import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;

public interface IUserService {
	// 获取用户profile
	UserProfileBean getUserProfile(String siteUserId);

	// 更新用户profile
	boolean updateProfile(UserProfileBean userProfileBean);

	// 获取用户列表
	List<SimpleUserBean> getUserList(int pageNum, int pageSize);

	// 封禁用户
	boolean sealUpUser(String siteUserId, int status);

}
