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
package com.akaxin.site.storage.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.akaxin.common.utils.TimeFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

public class SQLiteGroupProfileDao {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteGroupProfileDao.class);
    private static final String GROUP_PROFILE_TABLE = SQLConst.SITE_GROUP_PROFILE;

    private static SQLiteGroupProfileDao instance = new SQLiteGroupProfileDao();

    public static SQLiteGroupProfileDao getInstance() {
        return instance;
    }

    public List<SimpleGroupBean> queryGroupList(int pageNum, int pageSize) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "SELECT site_group_id,group_name,group_photo FROM " + GROUP_PROFILE_TABLE
                + " WHERE group_status>0 LIMIT ?,?;";
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        int startNum = (pageNum - 1) * pageSize;
        preStatement.setInt(1, startNum);
        preStatement.setInt(2, pageSize);
        ResultSet rs = preStatement.executeQuery();
        List<SimpleGroupBean> beanList = new ArrayList<SimpleGroupBean>();
        while (rs.next()) {
            SimpleGroupBean bean = new SimpleGroupBean();
            bean.setGroupId(rs.getString(1));
            bean.setGroupName(rs.getString(2));
            bean.setGroupPhoto(rs.getString(3));
            beanList.add(bean);
        }

        LogUtils.dbDebugLog(logger, startTime, beanList.size(), sql, pageNum, pageSize);
        return beanList;
    }

    public String getMaxGroupId() throws SQLException {
        long startTime = System.currentTimeMillis();
        long newGroupId = 10000;
        String sql = "SELECT max(id),site_group_id FROM " + GROUP_PROFILE_TABLE;
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        ResultSet rs = preStatement.executeQuery();
        if (rs != null) {
            long currentGroupId = rs.getLong(2);
            if (currentGroupId < 10000) {
                currentGroupId = 10000;
            }
            newGroupId = currentGroupId + 1;
        }

        LogUtils.dbDebugLog(logger, startTime, newGroupId, sql);
        return String.valueOf(newGroupId);
    }

    public GroupProfileBean saveGroupProfile(GroupProfileBean bean) throws SQLException {
        long startTime = System.currentTimeMillis();
        if (bean.getGroupId() == null) {
            bean.setGroupId(this.getMaxGroupId());
        }
        String sql = "INSERT INTO " + GROUP_PROFILE_TABLE
                + "(site_group_id,group_name,group_photo,group_notice,group_status,create_user_id,close_invite_group_chat,create_time) VALUES(?,?,?,?,1,?,?,?);";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, bean.getGroupId());
        preStatement.setString(2, bean.getGroupName());
        preStatement.setString(3, bean.getGroupPhoto());
        preStatement.setString(4, bean.getGroupNotice());
        preStatement.setString(5, bean.getCreateUserId());
        preStatement.setBoolean(6, true);// 默认允许群成员添加新的群聊成员
        preStatement.setLong(7, bean.getCreateTime());
        int result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getGroupId());
        return result > 0 ? bean : null;
    }

    public GroupProfileBean queryGroupProfile(String siteGroupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        GroupProfileBean profileBean = null;
        String sql = "SELECT site_group_id,group_name,group_photo,group_notice,ts_status,create_user_id,group_status,close_invite_group_chat,create_time FROM "
                + GROUP_PROFILE_TABLE + " WHERE site_group_id=?;";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, siteGroupId);
        ResultSet rs = preStatement.executeQuery();

        if (rs.next()) {
            profileBean = new GroupProfileBean();
            profileBean.setGroupId(rs.getString(1));
            profileBean.setGroupName(rs.getString(2));
            profileBean.setGroupPhoto(rs.getString(3));
            profileBean.setGroupNotice(rs.getString(4));
            profileBean.setTsStatus(rs.getInt(5));
            profileBean.setCreateUserId(rs.getString(6));
            profileBean.setGroupStatus(rs.getInt(7));
            profileBean.setCloseInviteGroupChat(rs.getBoolean(8));
            profileBean.setCreateTime(rs.getLong(9));
        }

        LogUtils.dbDebugLog(logger, startTime, profileBean, sql, siteGroupId);
        return profileBean;
    }

    public GroupProfileBean querySimpleGroupProfile(String siteGroupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        GroupProfileBean profileBean = null;
        String sql = "SELECT site_group_id,group_name,group_photo FROM " + GROUP_PROFILE_TABLE
                + " WHERE site_group_id=?;";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, siteGroupId);
        ResultSet rs = preStatement.executeQuery();

        if (rs.next()) {
            profileBean = new GroupProfileBean();
            profileBean.setGroupId(rs.getString(1));
            profileBean.setGroupName(rs.getString(2));
            profileBean.setGroupPhoto(rs.getString(3));
        }

        LogUtils.dbDebugLog(logger, startTime, profileBean, sql, siteGroupId);
        return profileBean;
    }

    /**
     * <pre>
     * status = 0:删除的群组
     * status = 1:正常的群
     * </pre>
     *
     * @param siteGroupId
     * @return
     * @throws SQLException
     */
    public int queryGroupStatus(String siteGroupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "SELECT group_status FROM " + GROUP_PROFILE_TABLE + " WHERE site_group_id=?;";
        int result = 0;

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, siteGroupId);
        ResultSet rs = preStatement.executeQuery();

        if (rs.next()) {
            result = rs.getInt(1);
        }

        LogUtils.dbDebugLog(logger, startTime, result, sql, siteGroupId);
        return result;
    }

    public int updateGroupProfile(GroupProfileBean bean) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "UPDATE " + GROUP_PROFILE_TABLE
                + " SET group_name=?, group_photo=?, group_notice=? WHERE site_group_id=?;";
        int result = 0;
        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, bean.getGroupName());
        preStatement.setString(2, bean.getGroupPhoto());
        preStatement.setString(3, bean.getGroupNotice());
        preStatement.setString(4, bean.getGroupId());
        result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getGroupName(), bean.getGroupPhoto(),
                bean.getGroupNotice(), bean.getGroupId());
        return result;
    }

    /**
     * 更新是否可以邀请群聊的状态值
     *
     * @param bean
     * @return
     * @throws SQLException
     */
    public int updateGroupIGC(GroupProfileBean bean) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "UPDATE " + GROUP_PROFILE_TABLE + " SET close_invite_group_chat=? WHERE site_group_id=?;";
        int result = 0;

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setBoolean(1, bean.isCloseInviteGroupChat());
        preStatement.setString(2, bean.getGroupId());
        result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, bean.isCloseInviteGroupChat(), bean.getGroupId());
        return result;
    }

    public int updateGroupOwer(String siteUserId, String groupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "UPDATE " + GROUP_PROFILE_TABLE + " SET create_user_id=? WHERE site_group_id=?;";
        int result = 0;

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, siteUserId);
        preStatement.setString(2, groupId);
        result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, groupId);
        return result;
    }

    public boolean deleteGroupProfile(String groupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "UPDATE " + GROUP_PROFILE_TABLE + " SET group_status=0 WHERE site_group_id=?;";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, groupId);
        int result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql);
        return result >= 1;
    }

    public String getGrouMaster(String groupId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String siteUserId = null;
        String sql = "SELECT create_user_id FROM " + GROUP_PROFILE_TABLE + " WHERE site_group_id=?;";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, groupId);

        ResultSet rs = preStatement.executeQuery();
        if (rs.next()) {
            siteUserId = rs.getString(1);
        }

        LogUtils.dbDebugLog(logger, startTime, siteUserId, sql, groupId);
        return siteUserId;
    }

    public int getGroupNum(long now,int day) throws SQLException {
        long startTime = System.currentTimeMillis();
        long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
        if (day != 0) {
            endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
        }

        String sql = "SELECT COUNT(*) FROM " + GROUP_PROFILE_TABLE +" WHERE create_time < ? ";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preparedStatement.setLong(1,endTimeOfDay);
        ResultSet resultSet = preparedStatement.executeQuery();
        int groupNum = resultSet.getInt(1);
        LogUtils.dbDebugLog(logger, startTime, groupNum, sql);
        return groupNum;

    }
}