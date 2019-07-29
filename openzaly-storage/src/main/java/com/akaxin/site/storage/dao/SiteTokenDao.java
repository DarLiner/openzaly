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
package com.akaxin.site.storage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.bean.ExpireToken;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 用户邀请码
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-11 17:26:52
 */
public class SiteTokenDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteTokenDao.class);

	private final String TOKEN_TABLE = SQLConst.SITE_EXPIRE_TOKEN;

	public static SiteTokenDao getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static SiteTokenDao instance = new SiteTokenDao();
	}

	/**
	 * 新增UIC码
	 * 
	 * @param bean
	 * @return
	 * @throws SQLException
	 */
	public boolean addToken(ExpireToken bean) throws SQLException {
		long startTime = System.currentTimeMillis();

		String sql = "INSERT INTO " + TOKEN_TABLE
				+ "(token,bid,btype,status,content,create_time,expire_time) VALUES(?,?,?,?,?,?,?);";

		int result;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getToken());
			ps.setString(2, bean.getBid());
			ps.setInt(3, bean.getBtype());
			ps.setInt(4, 1);// 0:失效 1:正常使用
			ps.setString(5, "");
			ps.setLong(6, bean.getCreateTime());
			ps.setLong(7, bean.getExpireTime());

			result = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getToken(), bean.getBid(), bean.getBtype(),
				bean.getStatus(), "", bean.getCreateTime(), bean.getExpireTime());
		return result > 0;
	}

	/**
	 * @param uic
	 * @return
	 * @throws SQLException
	 */
	public ExpireToken queryExpireToken(String token) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT token,bid,btype,status,create_time,expire_time FROM " + TOKEN_TABLE
				+ " WHERE token=? AND status>0;";

		ExpireToken bean = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, token);

			rs = pst.executeQuery();
			if (rs.next()) {
				bean = new ExpireToken();
				bean.setToken(rs.getString(1));
				bean.setBid(rs.getString(2));
				bean.setBtype(rs.getInt(3));
				bean.setStatus(rs.getInt(4));
				bean.setCreateTime(rs.getLong(5));
				bean.setExpireTime(rs.getLong(6));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, token);
		return bean;
	}

	public ExpireToken queryExpireTokenByBid(String bid, long time) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT token,bid,btype,status,create_time,expire_time FROM " + TOKEN_TABLE
				+ " WHERE bid=? AND status>0 AND create_time>time limit 1;";

		ExpireToken bean = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, bid);
			pst.setLong(2, time);

			rs = pst.executeQuery();
			if (rs.next()) {
				bean = new ExpireToken();
				bean.setToken(rs.getString(1));
				bean.setBid(rs.getString(2));
				bean.setBtype(rs.getInt(3));
				bean.setStatus(rs.getInt(4));
				bean.setCreateTime(rs.getLong(5));
				bean.setExpireTime(rs.getLong(6));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, bid, time);
		return bean;
	}

}
