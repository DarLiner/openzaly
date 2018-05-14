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

import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.utils.FilePathUtils;
import com.akaxin.site.storage.api.*;
import com.akaxin.site.storage.bean.*;
import com.akaxin.site.storage.service.*;
import com.akaxin.site.web.admin.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("userManageService")
public class UserManageService implements IUserService {
    private IMessageDao messageDao = new MessageDaoService();
    private IUserFriendDao friendDao = new UserFriendDaoService();
    private IGroupDao groupDao = new GroupDaoService();
    private IUserProfileDao profileDao = new UserProfileDaoService();
    private IUserDeviceDao deviceDao = new DeviceDaoService();
    private IUserSessionDao sessionDao = new UserSessionDaoService();
    private static final Logger logger = LoggerFactory.getLogger(UserManageService.class);

    @Override
    public UserProfileBean getUserProfile(String siteUserId) {
        UserProfileBean bean = UserProfileDao.getInstance().getUserProfileById(siteUserId);
        bean.setDefaultState(ifDefaultUser(bean.getSiteUserId()));
        return bean;
    }

    private int ifDefaultUser(String siteUserId) {
        List<String> userDefault = SiteConfigDao.getInstance().getUserDefault();
        if (userDefault != null && userDefault.size() > 0) {
            for (String site_User_Id : userDefault) {
                if (siteUserId.equals(site_User_Id)) {
                    return 1;
                }
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

    @Override
    public boolean delUser(String siteUserId) {
        boolean delProfile = false;
        ArrayList<String> userFileIds = new ArrayList<>();
        try {
            List<UserDeviceBean> userDeviceList = deviceDao.getUserDeviceList(siteUserId);
            for (UserDeviceBean userDeviceBean : userDeviceList) {
                sessionDao.deleteUserSession(siteUserId, userDeviceBean.getDeviceId());
            }
            UserProfileBean userProfileById = profileDao.getUserProfileById(siteUserId);
            String userPhoto = userProfileById.getUserPhoto();
            userFileIds.add(userPhoto);
            delProfile = profileDao.delUser(siteUserId) && deviceDao.delDevice(siteUserId);
        } catch (SQLException e) {
            logger.error("del user profile error", e);
        }
        try {
           List<String> msgList= messageDao.queryMessageFile(siteUserId);
            for (String fileId : msgList) {
                userFileIds.add(fileId);
            }
            messageDao.delUserMessage(siteUserId);
        } catch (SQLException e) {
            logger.error("del user Message error", e);
        }
        try {
            friendDao.delUserFriend(siteUserId);
        } catch (SQLException e) {
            logger.error("del user friend error", e);
        }
        try {
            List<SimpleGroupBean> userGroups = groupDao.getUserGroups(siteUserId);
            for (SimpleGroupBean userGroup : userGroups) {
                String groupMasterId = UserGroupDao.getInstance().getGroupMaster(userGroup.getGroupId());
                if (groupMasterId.equals(siteUserId)) {
                    userFileIds.add(userGroup.getGroupPhoto());
                    groupDao.rmGroupProfile(userGroup.getGroupId());
                } else {
                    ArrayList<String> delList = new ArrayList<>();
                    delList.add(siteUserId);
                    groupDao.deleteGroupMember(userGroup.getGroupId(), delList);
                }
            }
        } catch (SQLException e) {
            logger.error("del user group error", e);
        }
        for (String userFilePath : userFileIds) {
            if (userFilePath.startsWith("AKX-") || userFilePath.startsWith("akx-")) {
                userFilePath = userFilePath.substring(4, userFilePath.length());
            }
            File delFile = new File(FilePathUtils.getFilePathByFileId(userFilePath));
            if (delFile.exists()) {
                delFile.delete();
            }
        }
        return delProfile;
    }

}
