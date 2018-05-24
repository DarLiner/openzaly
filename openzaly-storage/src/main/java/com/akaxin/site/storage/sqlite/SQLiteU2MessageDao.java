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
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:23
 */
public class SQLiteU2MessageDao {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteU2MessageDao.class);
    private static final String USER2_MESSAGE_TABLE = SQLConst.SITE_USER_MESSAGE;
    private static final String USER2_MESSAGE_POINATER_TABLE = SQLConst.SITE_MESSAGE_POINTER;
    private static SQLiteU2MessageDao instance = new SQLiteU2MessageDao();

    public static SQLiteU2MessageDao getInstance() {
        return instance;
    }

    public boolean saveU2Message(U2MessageBean bean) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO " + USER2_MESSAGE_TABLE
                + "(site_user_id,msg_id,send_user_id,msg_type,content,device_id,ts_key,msg_time) VALUES(?,?,?,?,?,?,?,?);";

        PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preStatement.setString(1, bean.getSiteUserId());
        preStatement.setString(2, bean.getMsgId());
        preStatement.setString(3, bean.getSendUserId());
        preStatement.setLong(4, bean.getMsgType());
        preStatement.setString(5, bean.getContent());
        preStatement.setString(6, bean.getDeviceId());
        preStatement.setString(7, bean.getTsKey());
        preStatement.setLong(8, bean.getMsgTime());
        int result = preStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getMsgId(), bean.getSendUserId(),
                bean.getMsgType(), bean.getContent(), bean.getDeviceId(), bean.getTsKey(), bean.getMsgTime());
        return result == 1;
    }

    public List<U2MessageBean> getU2Message(String userId, String deviceId, long start, long limit)
            throws SQLException {
        long startTime = System.currentTimeMillis();
        List<U2MessageBean> msgList = new ArrayList<U2MessageBean>();
        String sql = "SELECT id,site_user_id,msg_id,send_user_id,msg_type,content,device_id,ts_key,msg_time FROM "
                + USER2_MESSAGE_TABLE + " WHERE site_user_id=? AND id>? LIMIT ?;";

        long pointer = queryU2MessagePointer(userId, deviceId);
        start = start > pointer ? start : pointer;

        PreparedStatement statement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        statement.setString(1, userId);
        statement.setLong(2, start);
        statement.setLong(3, limit);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            U2MessageBean u2MsgBean = new U2MessageBean();
            u2MsgBean.setId(rs.getInt(1));
            u2MsgBean.setSiteUserId(rs.getString(2));
            u2MsgBean.setMsgId(rs.getString(3));
            u2MsgBean.setSendUserId(rs.getString(4));
            u2MsgBean.setMsgType(rs.getInt(5));
            u2MsgBean.setContent(rs.getString(6));
            u2MsgBean.setDeviceId(rs.getString(7));
            u2MsgBean.setTsKey(rs.getString(8));
            u2MsgBean.setMsgTime(rs.getLong(9));

            msgList.add(u2MsgBean);
        }

        LogUtils.dbDebugLog(logger, startTime, msgList.size(), sql, userId, start, limit);
        return msgList;
    }

    public long queryU2MessagePointer(String siteUserId, String deviceId) throws SQLException {
        long startTime = System.currentTimeMillis();
        long pointer = 0;
        String sql = "SELECT pointer FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id=? AND device_id=?;";

        PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        pStatement.setString(1, siteUserId);
        pStatement.setString(2, deviceId);
        ResultSet prs = pStatement.executeQuery();
        if (prs.next()) {
            pointer = prs.getLong(1);
        }

        LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId, deviceId);
        return pointer == 0 ? queryMaxU2MessagePointer(siteUserId) - 10 : pointer;
    }

    public long queryMaxU2MessagePointer(String siteUserId) throws SQLException {
        long startTime = System.currentTimeMillis();
        long pointer = 0;
        String sql = "SELECT max(pointer) FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id=?;";

        PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        pStatement.setString(1, siteUserId);
        ResultSet prs = pStatement.executeQuery();
        if (prs.next()) {
            pointer = prs.getLong(1);
        }

        LogUtils.dbDebugLog(logger, startTime, pointer, sql, siteUserId);
        return pointer;
    }

    /**
     * 查找最大的消息id，消息id在游标表中为游标
     *
     * @param siteUserId
     * @return
     * @throws SQLException
     */
    public long queryMaxU2MessageId(String siteUserId) throws SQLException {
        long startTime = System.currentTimeMillis();
        long maxPointer = 0;
        String sql = "SELECT max(id) FROM " + USER2_MESSAGE_TABLE + " WHERE site_user_id=?;";

        PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        pStatement.setString(1, siteUserId);
        ResultSet prs = pStatement.executeQuery();
        if (prs.next()) {
            maxPointer = prs.getLong(1);
        }

        LogUtils.dbDebugLog(logger, startTime, maxPointer, sql, siteUserId);
        return maxPointer;
    }

    public boolean updateU2MessagePointer(String userId, String deviceId, long finish) throws SQLException {
        if (checkMsgPointer(userId, deviceId)) {
            return updateU2Pointer(userId, deviceId, finish);
        } else {
            return addU2Pointer(userId, deviceId, finish);
        }

    }

    public boolean addU2Pointer(String siteUserId, String deviceId, long finish) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO " + USER2_MESSAGE_POINATER_TABLE + "(site_user_id,pointer,device_id) VALUES(?,?,?)";

        PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        pStatement.setString(1, siteUserId);
        pStatement.setLong(2, finish);
        pStatement.setString(3, deviceId);
        int result = pStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, finish, deviceId);
        return result == 1;
    }

    public boolean updateU2Pointer(String siteUserId, String deviceId, long finish) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "UPDATE " + USER2_MESSAGE_POINATER_TABLE + " SET pointer=? WHERE site_user_id=? AND device_id=?;";

        PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        pStatement.setLong(1, finish);
        pStatement.setString(2, siteUserId);
        pStatement.setString(3, deviceId);
        int result = pStatement.executeUpdate();

        LogUtils.dbDebugLog(logger, startTime, result, sql, finish, siteUserId, deviceId);
        return result == 1;
    }

    public boolean checkMsgPointer(String siteUserId, String deviceId) {
        String querySql = "select pointer from " + USER2_MESSAGE_POINATER_TABLE
                + " WHERE site_user_id=? AND device_id=?";
        Long pointer = null;
        long startTime = System.currentTimeMillis();
        try {
            PreparedStatement pStatement = SQLiteJDBCManager.getConnection().prepareStatement(querySql);
            pStatement.setString(1, siteUserId);
            pStatement.setString(2, deviceId);

            ResultSet rs = pStatement.executeQuery();
            if (rs.next()) {
                pointer = rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("check msg pointer error. siteUserId={} deviceI={}", siteUserId, deviceId);
        }

        LogUtils.dbDebugLog(logger, startTime, pointer, siteUserId, deviceId);
        return pointer != null;
    }

    public int queryNumMessagePerDay(long now, int day) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "SELECT COUNT(*) FROM " + USER2_MESSAGE_TABLE + " WHERE msg_time BETWEEN ? and ? ";
        long startTimeOfDay = TimeFormats.getStartTimeOfDay(now);
        long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
        if (day != 0) {
            startTimeOfDay = startTimeOfDay - TimeUnit.DAYS.toMillis(day);
            endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
        }
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preparedStatement.setLong(1, startTimeOfDay);
        preparedStatement.setLong(2, endTimeOfDay);
        ResultSet resultSet = preparedStatement.executeQuery();
        int u2Count = resultSet.getInt(1);
        LogUtils.dbDebugLog(logger, startTime, u2Count, sql);
        return u2Count;
    }

    public boolean delUserMessage(String siteUserId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "DELETE FROM " + USER2_MESSAGE_TABLE + " WHERE site_user_id =? or send_user_id =?";
        String sqlP = "DELETE FROM " + USER2_MESSAGE_POINATER_TABLE + " WHERE site_user_id =? ";
        PreparedStatement statement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        statement.setString(1, siteUserId);
        statement.setString(2, siteUserId);
        int res1 = statement.executeUpdate();
        PreparedStatement statementP = SQLiteJDBCManager.getConnection().prepareStatement(sqlP);
        statementP.setString(1, siteUserId);
        int res2 = statementP.executeUpdate();
        if (res1 > 0 && res2 > 0) {
            LogUtils.dbDebugLog(logger, startTime, res1+","+res2, sql, "true");
            return true;
        }
        LogUtils.dbDebugLog(logger, startTime, res1+","+res2, sql, "false");

        return false;
    }

    public List<String> queryMessageFile(String siteUserId) throws SQLException {
        long startTime = System.currentTimeMillis();
        String sql = "select content from (select * from " + USER2_MESSAGE_TABLE + " where msg_type in (7,8,11,12)) t where site_user_id = ? or  send_user_id = ?";
        PreparedStatement preparedStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
        preparedStatement.setString(1,siteUserId);
        preparedStatement.setString(2, siteUserId);
        ResultSet rs = preparedStatement.executeQuery();
        ArrayList<String> u2Files = new ArrayList<>();
        while (rs.next()) {
            u2Files.add(rs.getString(1));
        }
        LogUtils.dbDebugLog(logger, startTime, rs, sql);
        return u2Files;
    }
}
