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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.storage.bean.ApplyFriendBean;
import com.akaxin.site.storage.bean.ApplyUserBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 好友申请相关数据库操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-19 20:00:19
 */
public class SiteFriendApplyDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteFriendApplyDao.class);
	private static final String FRIEND_APPLY_TABLE = SQLConst.SITE_FRIEND_APPLY;

	private static SiteFriendApplyDao instance = new SiteFriendApplyDao();

	public static SiteFriendApplyDao getInstance() {
		return instance;
	}

	public boolean saveApply(String siteUserId, String siteFriendId, String applyReason) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "INSERT INTO " + FRIEND_APPLY_TABLE
				+ "(site_user_id,site_friend_id,apply_reason,apply_time) VALUES(?,?,?,?);";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setString(2, siteFriendId);
			ps.setString(3, applyReason);
			ps.setLong(4, System.currentTimeMillis());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId, siteFriendId);
		return result > 0;
	}

	public boolean deleteApply(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "DELETE FROM " + FRIEND_APPLY_TABLE + " WHERE site_user_id=? AND site_friend_id=?;";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);
			ps.setString(2, siteFriendId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId);
		return result > 0;
	}

	public int getApplyCount(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int num = 0;
		String sql = "SELECT COUNT(site_user_id) FROM " + FRIEND_APPLY_TABLE
				+ " WHERE site_user_id=? AND site_friend_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, siteFriendId);

			rs = pst.executeQuery();
			if (rs.next()) {
				num = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, num, sql, siteUserId, siteFriendId);
		return num;
	}

	public int getApplyCount(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		int num = 0;
		String sql = "SELECT COUNT(distinct site_friend_id) FROM " + FRIEND_APPLY_TABLE + " WHERE site_user_id=? ";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				num = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, num, sql, siteUserId);
		return num;
	}

	/**
	 * 
	 * @param siteUserId
	 *            被请求者
	 * @param siteFriendId
	 *            请求者
	 * @return
	 * @throws SQLException
	 */
	public ApplyFriendBean getApplyInfo(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		// String sql = "SELECT site_user_id,site_friend_id,apply_reason,MAX(apply_time)
		// FROM " + FRIEND_APPLY_TABLE
		// + " WHERE site_user_id=? AND site_friend_id=?;";
		String sql = "SELECT a.site_user_id,a.site_friend_id,a.apply_reason,a.apply_time from " + FRIEND_APPLY_TABLE
				+ " a INNER JOIN (select site_friend_id,max(apply_time) apply_time from " + FRIEND_APPLY_TABLE
				+ " WHERE site_user_id=? and site_friend_id=? GROUP BY site_friend_id) AS b ON a.site_friend_id=b.site_friend_id AND a.apply_time=b.apply_time WHERE a.site_user_id=?;";

		ApplyFriendBean bean = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, siteFriendId);
			pst.setString(3, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				bean = new ApplyFriendBean();
				bean.setSiteUserId(rs.getString(1));
				bean.setSiteFriendId(rs.getString(2));
				bean.setApplyInfo(rs.getString(3));
				bean.setApplyTime(rs.getLong(4));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, siteUserId, siteFriendId);
		return bean;
	}

	/**
	 * 查询好友申请列表
	 * 
	 * @param siteUserId
	 * @return
	 * @throws SQLException
	 */
	public List<ApplyUserBean> queryApplyUsers(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<ApplyUserBean> applyUsers = new ArrayList<ApplyUserBean>();
		// String sql = "SELECT
		// a.site_friend_id,b.user_name,b.user_photo,a.apply_reason,max(a.apply_time)
		// FROM "
		// + FRIEND_APPLY_TABLE + " AS a LEFT JOIN " + SQLConst.SITE_USER_PROFILE
		// + " AS b ON a.site_friend_id=b.site_user_id WHERE a.site_user_id=? GROUP BY
		// a.site_friend_id;";

		String sql = "SELECT a.site_friend_id,b.user_name,b.user_photo,a.apply_reason,a.apply_time from (select c.site_friend_id,c.apply_reason,c.apply_time from "
				+ FRIEND_APPLY_TABLE + " c inner join (select site_friend_id,max(apply_time) apply_time from "
				+ FRIEND_APPLY_TABLE
				+ " where site_user_id=? group by site_friend_id) as d on c.site_friend_id=d.site_friend_id and c.apply_time=d.apply_time) AS a LEFT JOIN "
				+ SQLConst.SITE_USER_PROFILE + " AS b ON a.site_friend_id=b.site_user_id;";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getSlaveConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			while (rs.next()) {
				ApplyUserBean userBean = new ApplyUserBean();
				userBean.setUserId(rs.getString(1));
				userBean.setUserName(rs.getString(2));
				userBean.setUserPhoto(rs.getString(3));
				userBean.setApplyReason(rs.getString(4));
				userBean.setApplyTime(rs.getLong(5));
				applyUsers.add(userBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, applyUsers.size(), sql, siteUserId);
		return applyUsers;
	}
}
