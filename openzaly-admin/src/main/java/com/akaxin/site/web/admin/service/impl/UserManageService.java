/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.web.admin.service.impl;

import java.util.List;

import com.akaxin.site.business.dao.SiteConfigDao;
import org.springframework.stereotype.Service;

import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.web.admin.service.IUserService;

@Service("userManageService")
public class UserManageService implements IUserService {

    @Override
    public UserProfileBean getUserProfile(String siteUserId) {
        UserProfileBean bean = UserProfileDao.getInstance().getUserProfileById(siteUserId);
        bean.setDefaultState(ifDefaultUser(bean.getSiteUserId()));
        return bean;
    }

    private int ifDefaultUser(String siteUserId) {
        List<UserProfileBean> userDefault = SiteConfigDao.getInstance().getUserDefault();
        for (UserProfileBean bean : userDefault) {
            if (siteUserId.equals(bean.getSiteUserId())) {
                return 1;
            }
        }
        return 0;
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
