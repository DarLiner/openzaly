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
import jdk.nashorn.internal.ir.IfNode;
import org.springframework.stereotype.Service;

import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.web.admin.service.IGroupService;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-17 18:59:24
 */
@Service("groupManageService")
public class GroupManageService implements IGroupService {

    @Override
    public GroupProfileBean getGroupProfile(String siteGroupId) {
        GroupProfileBean groupProfile = UserGroupDao.getInstance().getGroupProfile(siteGroupId);
        groupProfile.setDefaultState(ifDefaultGroup(groupProfile.getGroupId()));
        return groupProfile;
    }

    private int ifDefaultGroup(String groupId) {
        List<String> groupDefault = SiteConfigDao.getInstance().getGroupDefault();
        if (groupDefault != null && groupDefault.size() > 0) {
            for (String s : groupDefault) {
                if (groupId.equals(s)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean updateGroupProfile(GroupProfileBean bean) {
        return UserGroupDao.getInstance().updateGroupProfile(bean);
    }

    @Override
    public List<SimpleGroupBean> getGroupList(int pageNum, int pageSize) {
        return UserGroupDao.getInstance().getGroupList(pageNum, pageSize);
    }

    @Override
    public List<GroupMemberBean> getGroupMembers(String siteGroupId, int pageNum, int pageSize) {
        return UserGroupDao.getInstance().getGroupMemberList(siteGroupId, pageNum, pageSize);
    }

    @Override
    public List<GroupMemberBean> getNonGroupMembers(String siteGroupId, int pageNum, int pageSize) {
        return UserGroupDao.getInstance().getNonGroupMemberList(siteGroupId, pageNum, pageSize);
    }

    @Override
    public boolean addGroupMembers(String siteGroupId, List<String> newMemberList) {
        return UserGroupDao.getInstance().addGroupMember(null, siteGroupId, newMemberList);
    }

    @Override
    public boolean removeGroupMembers(String siteGroupId, List<String> groupMemberList) {
        return UserGroupDao.getInstance().deleteGroupMember(siteGroupId, groupMemberList);
    }

    @Override
    public boolean dismissGroup(String siteGroupId) {
        return UserGroupDao.getInstance().deleteGroup(siteGroupId);
    }

    @Override
    public boolean setGroupDefault(String siteGroupId) {
        List<String> defaultList = SiteConfigDao.getInstance().getGroupDefault();
        if (defaultList != null && defaultList.size() >= 5) {
            return false;
        }
        boolean flag = SiteConfigDao.getInstance().updateGroupDefault(siteGroupId);
        if (flag) {
            return flag;
        } else {

            return SiteConfigDao.getInstance().setGroupDefault(siteGroupId);
        }
    }

    @Override
    public boolean delUserDefault(String siteGroupId) {
        List<String> groupDefault = SiteConfigDao.getInstance().getGroupDefault();
        StringBuffer stringBuffer = new StringBuffer();
        if (groupDefault.contains(siteGroupId)) {
            groupDefault.remove(siteGroupId);
            for (String s : groupDefault) {
                stringBuffer.append(s);
                stringBuffer.append(",");
            }
            if (groupDefault.size() == 0) {
                String del = null;
                return SiteConfigDao.getInstance().delGroupDefault(del);
            }
            int i = stringBuffer.lastIndexOf(",");
            stringBuffer.delete(i, i + 1);
            String s = stringBuffer.toString();
            return SiteConfigDao.getInstance().delGroupDefault(s);
        }

        return false;
    }

}
