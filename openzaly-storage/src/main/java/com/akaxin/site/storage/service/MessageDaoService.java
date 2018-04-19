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
package com.akaxin.site.storage.service;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.sqlite.SQLiteGroupMessageDao;
import com.akaxin.site.storage.sqlite.SQLiteU2MessageDao;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:33
 */
public class MessageDaoService implements IMessageDao {

    @Override
    public boolean saveU2Message(U2MessageBean u2Bean) throws SQLException {
        return SQLiteU2MessageDao.getInstance().saveU2Message(u2Bean);
    }

    @Override
    public boolean updateU2Pointer(String id, String deviceId, long finish) throws SQLException {
        return SQLiteU2MessageDao.getInstance().updateU2MessagePointer(id, deviceId, finish);
    }

    @Override
    public List<U2MessageBean> queryU2Message(String id, String deviceId, long start, long limit) throws SQLException {
        return SQLiteU2MessageDao.getInstance().getU2Message(id, deviceId, start, limit);
    }

    @Override
    public long queryU2Pointer(String userId, String deviceId) throws SQLException {
        return SQLiteU2MessageDao.getInstance().queryU2MessagePointer(userId, deviceId);
    }

    @Override
    public long queryMaxU2Pointer(String userId) throws SQLException {
        return SQLiteU2MessageDao.getInstance().queryMaxU2MessagePointer(userId);
    }

    @Override
    public long queryMaxU2MessageId(String userId) throws SQLException {
        return SQLiteU2MessageDao.getInstance().queryMaxU2MessageId(userId);
    }

    @Override
    public boolean saveGroupMessage(GroupMessageBean gmsgBean) throws SQLException {
        return SQLiteGroupMessageDao.getInstance().saveGroupMessage(gmsgBean);
    }

    @Override
    public boolean updateGroupPointer(String gid, String userId, String deviceId, long finish) throws SQLException {
        return SQLiteGroupMessageDao.getInstance().updateGroupMessagePointer(gid, userId, deviceId, finish);
    }

    @Override
    public List<GroupMessageBean> queryGroupMessage(String groupId, String userId, String deviceId, long start,
                                                    int limit) throws SQLException {
        return SQLiteGroupMessageDao.getInstance().queryGroupMessage(groupId, userId, deviceId, start, limit);
    }

    @Override
    public long queryGroupMessagePointer(String groupId, String siteUserId, String deviceId) throws SQLException {
        return SQLiteGroupMessageDao.getInstance().queryGroupPointer(groupId, siteUserId, deviceId, 0);
    }

    @Override
    public long queryMaxGroupPointer(String groupId) throws SQLException {
        return SQLiteGroupMessageDao.getInstance().queryMaxGroupPointer(groupId);
    }

    @Override
    public long queryMaxUserGroupPointer(String groupId, String siteUserId) throws SQLException {
        return SQLiteGroupMessageDao.getInstance().queryMaxUserGroupPointer(groupId, siteUserId);
    }

    @Override
    public int queryU2MessagePerDay(long now,int day) throws SQLException {
        int u2Count = SQLiteU2MessageDao.getInstance().queryNumMessagePerDay(now,day);
        return u2Count;
    }
}
