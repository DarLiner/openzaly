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
package com.akaxin.site.storage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 获取站点所有用户
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-09 20:19:59
 */
public class SiteUsersDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUsersDao.class);
	private final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;

	private static class SingletonHolder {
		private static SiteUsersDao instance = new SiteUsersDao();
	}

	public static SiteUsersDao getInstance() {
		return SingletonHolder.instance;
	}

	public List<String> querySiteUserId(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<String> siteUserList = new ArrayList<String>();
		String sql = "SELECT site_user_id FROM " + USER_PROFILE_TABLE + " LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, startNum);
			pst.setInt(2, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				siteUserList.add(rs.getString(1));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, siteUserList.size(), sql, pageNum, pageSize);
		return siteUserList;
	}

}