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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 获取站点所有用户
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-09 20:19:59
 */
public class SQLiteSiteUsersDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteSiteUsersDao.class);
	private final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;

	private static class SingletonHolder {
		private static SQLiteSiteUsersDao instance = new SQLiteSiteUsersDao();
	}

	public static SQLiteSiteUsersDao getInstance() {
		return SingletonHolder.instance;
	}

	public List<String> querySiteUserId(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> siteUserList = new ArrayList<String>();
		String sql = "SELECT site_user_id FROM " + USER_PROFILE_TABLE + " LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, startNum);
		preStatement.setInt(2, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			siteUserList.add(rs.getString(1));
		}

		LogUtils.dbDebugLog(logger, startTime, siteUserList.size(), sql, pageNum, pageSize);
		return siteUserList;
	}

}