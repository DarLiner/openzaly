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
package com.akaxin.site.storage.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:14:45
 */
public class SQLiteUserFriendDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUserFriendDao.class);
	private static final String USER_FRIEND_TABLE = SQLConst.SITE_USER_FRIEND;
	private static SQLiteUserFriendDao instance = new SQLiteUserFriendDao();

	public static SQLiteUserFriendDao getInstance() {
		return instance;
	}

	public boolean saveRelation(String siteUserId, String siteFriendId, int relation) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "INSERT INTO " + USER_FRIEND_TABLE
				+ "(site_user_id,site_friend_id,relation,add_time) VALUES(?,?,?,?);";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);
		preState.setInt(3, relation);
		preState.setLong(4, System.currentTimeMillis());
		result = preState.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "", sql);

		return result > 0;
	}

	public boolean updateRelation(String siteUserId, String siteFriendId, int relation) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_FRIEND_TABLE
				+ " SET relation=?, add_time=? WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setInt(1, relation);
		preState.setLong(2, System.currentTimeMillis());
		preState.setString(3, siteUserId);
		preState.setString(4, siteFriendId);
		result = preState.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result,
				sql + relation + "," + System.currentTimeMillis() + "," + siteUserId + "," + siteFriendId);

		return result > 0;
	}

	public int queryRelation(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int relation = 0;
		String sql = "SELECT relation FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);

		ResultSet rs = preState.executeQuery();
		if (rs.next()) {
			relation = rs.getInt(1);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, relation + "", sql + siteUserId + "," + siteFriendId);

		return relation;
	}

	public boolean deleteRelation(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "DELETE FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);
		result = preState.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "", sql + siteUserId + "," + siteFriendId);

		return result > 0;
	}

	public UserFriendBean queryUserFriendSetting(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT mute FROM " + USER_FRIEND_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";
		
		UserFriendBean bean = null;
		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setString(1, siteUserId);
		preState.setString(2, siteFriendId);

		ResultSet rs = preState.executeQuery();
		if (rs.next()) {
			bean = new UserFriendBean();
			bean.setMute(rs.getBoolean(1));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, bean, sql + siteUserId + "," + siteFriendId);

		return bean;
	}

	public boolean updateUserFriendSetting(String siteUserId, UserFriendBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_FRIEND_TABLE + " SET mute=? WHERE site_user_id=? AND site_friend_id=?;";

		PreparedStatement preState = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preState.setBoolean(1, bean.isMute());
		preState.setString(2, siteUserId);
		preState.setString(3, bean.getSiteUserId());
		result = preState.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + siteUserId + "," + bean.toString());

		return result > 0;
	}
}
