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
package com.akaxin.admin.site.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akaxin.admin.site.service.IUserService;
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
