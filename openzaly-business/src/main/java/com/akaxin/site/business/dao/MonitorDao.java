package com.akaxin.site.business.dao;

import com.akaxin.site.storage.api.*;
import com.akaxin.site.storage.service.*;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.DayOfWeek;

public class MonitorDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserProfileDao.class);
    private static MonitorDao instance = new MonitorDao();
    private IMessageDao messageDao = new MessageDaoService();
    private IUserProfileDao userProfileDao = new UserProfileDaoService();
    private IUserGroupDao userGroupDao = new UserGroupDaoService();
    private IGroupDao groupDao = new GroupDaoService();
    private IUserFriendDao friendDao = new UserFriendDaoService();


    public static MonitorDao getInstance() {
        return instance;
    }

    //监控查询状态(消息数量)
    public int queryNumMessagePerDay(long time,int day) {
        try {
            int groupMessagePerDay = userGroupDao.queryGroupMessagePerDay(time,day);
            int u2MessagePerDay = messageDao.queryU2MessagePerDay(time,day);
            return groupMessagePerDay + u2MessagePerDay;
        } catch (SQLException e) {
            logger.error("query Num of Message Per Day error.", e);
        }
        return -1;
    }

    //监控查询状态(单群组)
    public int queryGroupMessagePerDay(long now,int day) {
        try {
            return userGroupDao.queryGroupMessagePerDay(now,day);
        } catch (SQLException e) {
            logger.error("query Num of Message Group Only error.", e);
        }
        return -1;
    }

    //监控查询状态(单个人)
    public int queryU2MessagePerDay(long now,int day) {
        try {
            return messageDao.queryU2MessagePerDay(now,day);
        } catch (SQLException e) {
            logger.error("query Num of Message U2 Only error.", e);
        }
        return -1;

    }

    //监控查询状态(注册人数)
    public int queryNumRegisterPerDay(long now,int day) {
        try {
            return userProfileDao.queryNumRegisterPerDay(now,day);
        } catch (SQLException e) {
            logger.error("query Num of Register Per Day error.", e);
        }
        return -1;
    }

    //查询全部用户数
    public int getSiteUserNum(long now,int day) {
        try {
            return userProfileDao.getUserNum(now,day);
        } catch (SQLException e) {
            logger.error("query Num of SiteUser error", e);
        }
        return -1;
    }

    //查询全部群组数量
    public int getGroupNum(long now,int day) {
        try {
            return groupDao.getGroupNum(now,day);
        } catch (SQLException e) {
            logger.error("query Num of Group error.", e);
        }
        return -1;
    }


    public int friendNum(long now,int day) {
        try {
            return friendDao.friendNum(now,day);
        } catch (SQLException e) {
            logger.error("query Num of friend error.",e);
        }
        return -1;
    }
}
