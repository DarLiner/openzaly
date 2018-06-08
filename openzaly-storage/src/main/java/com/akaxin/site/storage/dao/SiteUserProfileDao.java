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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.TimeFormats;
import com.akaxin.proto.core.UserProto;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.util.SqlUtils;

/**
 * 用户个人资料表(db_table:site_user_profile)相关操作
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-09 20:19:59
 */
public class SiteUserProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUserProfileDao.class);
	private final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;
	private final String USER_FRIEND_TABLE = SQLConst.SITE_USER_FRIEND;
	private static SiteUserProfileDao instance = new SiteUserProfileDao();

	public static SiteUserProfileDao getInstance() {
		return instance;
	}

	// save Profile
	public boolean saveProfile(UserProfileBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_PROFILE_TABLE // 用户profile表
				+ "(site_user_id," // 站点ID
				+ "global_user_id," // 全局ID
				+ "user_id_pubk," // 用户公钥
				+ "user_name,"// 用户昵称 nickname
				+ "user_name_in_latin,"// 昵称拼音
				+ "user_photo,"// 用户头像
				+ "phone_id,"// 手机号
				+ "user_status,"// 用户状态
				+ "self_introduce,"// 自我介绍
				+ "apply_info,"// 申请信息
				+ "register_time) VALUES(?,?,?,?,?,?,?,?,?,?,?);";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();

			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getSiteUserId());
			ps.setString(2, bean.getGlobalUserId());
			ps.setString(3, bean.getUserIdPubk());
			ps.setString(4, bean.getUserName());
			ps.setString(5, bean.getUserNameInLatin());
			ps.setString(6, bean.getUserPhoto());
			ps.setString(7, bean.getPhoneId());
			ps.setInt(8, bean.getUserStatus());
			ps.setString(9, bean.getSelfIntroduce());
			ps.setString(10, bean.getApplyInfo());
			ps.setLong(11, bean.getRegisterTime());

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getUserIdPubk(),
				bean.getUserName(), bean.getUserPhoto(), bean.getPhoneId(), bean.getUserStatus(),
				bean.getSelfIntroduce(), bean.getApplyInfo(), bean.getRegisterTime(), bean.getGlobalUserId());
		return result == 1;
	}

	// globalUserId -> siteUserId
	public String querySiteUserIdByGlobalUserId(String globalUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String siteUserId = null;
		String sql = "SELECT site_user_id FROM " + SQLConst.SITE_USER_PROFILE + " WHERE global_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, globalUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				siteUserId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, siteUserId, sql, globalUserId);
		return siteUserId;
	}

	// userIdPubk -> siteUserId
	public String querySiteUserIdByPubk(String userIdPubk) throws SQLException {
		long startTime = System.currentTimeMillis();
		String siteUserId = null;
		String sql = "SELECT site_user_id FROM " + SQLConst.SITE_USER_PROFILE + " WHERE user_id_pubk=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, userIdPubk);

			rs = pst.executeQuery();
			if (rs.next()) {
				siteUserId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, siteUserId, sql, userIdPubk);
		return siteUserId;
	}

	// lowerCaseLoginId -> siteUserId
	public String querySiteUserIdByLowercaseLoginId(String lowercaseLoginId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String globalUserId = null;
		String sql = "SELECT site_user_id FROM " + USER_PROFILE_TABLE + " WHERE login_id_lowercase=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, lowercaseLoginId);

			rs = pst.executeQuery();
			if (rs.next()) {
				globalUserId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, globalUserId, sql, lowercaseLoginId);
		return globalUserId;
	}

	// siteUserId -> gloabalUserId
	public String queryGlobalUserIdBySiteUserId(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String globalUserId = null;
		String sql = "SELECT global_user_id FROM " + USER_PROFILE_TABLE + " WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				globalUserId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, globalUserId, sql, siteUserId);
		return globalUserId;
	}

	// siteUserId -> siteLoginId
	public String querySiteLoginIdBySiteUserId(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String globalUserId = null;
		String sql = "SELECT site_login_id FROM " + USER_PROFILE_TABLE + " WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				globalUserId = rs.getString(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, globalUserId, sql, siteUserId);
		return globalUserId;
	}

	// siteUserId -> Simple Profile
	public SimpleUserBean querySimpleProfileById(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE site_user_id=?;";

		SimpleUserBean userBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new SimpleUserBean();
				userBean.setUserId(rs.getString(1));
				userBean.setUserName(rs.getString(2));
				userBean.setUserPhoto(rs.getString(3));
				userBean.setUserStatus(rs.getInt(4));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userBean, sql, siteUserId);
		return userBean;
	}

	// globalUserId -> Simple Profile
	public SimpleUserBean querySimpleProfileByGlobalUserId(String globalUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		SimpleUserBean userBean = new SimpleUserBean();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE global_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, globalUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean.setUserId(rs.getString(1));
				userBean.setUserName(rs.getString(2));
				userBean.setUserPhoto(rs.getString(3));
				userBean.setUserStatus(rs.getInt(4));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userBean.toString(), sql, globalUserId);
		return userBean;
	}

	// userIdPubk -> Simple Profile
	public SimpleUserBean querySimpleProfileByPubk(String userIdPubk) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE user_id_pubk=?;";

		SimpleUserBean userBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, userIdPubk);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new SimpleUserBean();
				userBean.setUserId(rs.getString(1));
				userBean.setUserName(rs.getString(2));
				userBean.setUserPhoto(rs.getString(3));
				userBean.setUserStatus(rs.getInt(4));
			}
		} catch (Exception e) {
			throw e;
		}

		LogUtils.dbDebugLog(logger, startTime, userBean, sql, userIdPubk);
		return userBean;
	}

	// siteUserId -> Full Profile
	public UserProfileBean queryUserProfileById(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,site_login_id,user_id_pubk,user_name,user_photo,self_introduce,user_status,register_time,phone_id FROM "
				+ USER_PROFILE_TABLE + " WHERE site_user_id=?;";

		UserProfileBean userBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new UserProfileBean();
				userBean.setSiteUserId(rs.getString(1));
				userBean.setSiteLoginId(rs.getString(2));
				userBean.setUserIdPubk(rs.getString(3));
				userBean.setUserName(rs.getString(4));
				userBean.setUserPhoto(rs.getString(5));
				userBean.setSelfIntroduce(rs.getString(6));
				userBean.setUserStatus(rs.getInt(7));
				userBean.setRegisterTime(rs.getLong(8));
				userBean.setPhoneId(rs.getString(9));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userBean, sql, siteUserId);
		return userBean;
	}

	// siteUserId,siteFriendId -> Friend Full Profile
	public UserFriendBean queryFriendProfileById(String siteUserId, String siteFriendId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT a.site_user_id,a.site_login_id,a.user_id_pubk,a.user_name,a.user_name_in_latin,a.user_photo,a.self_introduce,a.user_status,a.register_time,b.mute,b.alias_name,b.alias_name_in_latin FROM "
				+ USER_PROFILE_TABLE
				+ " AS a LEFT JOIN (SELECT site_user_id,site_friend_id,mute,alias_name,alias_name_in_latin FROM "
				+ USER_FRIEND_TABLE
				+ " WHERE site_user_id=?) AS b ON a.site_user_id=b.site_friend_id WHERE a.site_user_id=?;";

		UserFriendBean userBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setString(2, siteFriendId);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new UserFriendBean();
				userBean.setSiteUserId(rs.getString(1));
				userBean.setSiteLoginId(rs.getString(2));
				userBean.setUserIdPubk(rs.getString(3));
				userBean.setUserName(rs.getString(4));
				userBean.setUserNameInLatin(rs.getString(5));
				userBean.setUserPhoto(rs.getString(6));
				userBean.setSelfIntroduce(rs.getString(7));
				userBean.setUserStatus(rs.getInt(8));
				userBean.setRegisterTime(rs.getLong(9));
				userBean.setMute(rs.getBoolean(10));
				userBean.setAliasName(rs.getString(11));
				userBean.setAliasNameInLatin(rs.getString(12));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userBean, sql, siteUserId, siteFriendId);
		return userBean;
	}

	// globalUserId -> Full Profile
	public UserProfileBean queryUserProfileByGlobalUserId(String globalUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,site_login_id,user_id_pubk,user_name,user_photo,self_introduce,user_status,register_time FROM "
				+ USER_PROFILE_TABLE + " WHERE global_user_id=?;";

		UserProfileBean userBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, globalUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new UserProfileBean();
				userBean.setSiteUserId(rs.getString(1));
				userBean.setSiteLoginId(rs.getString(2));
				userBean.setUserIdPubk(rs.getString(3));
				userBean.setUserName(rs.getString(4));
				userBean.setUserPhoto(rs.getString(5));
				userBean.setSelfIntroduce(rs.getString(6));
				userBean.setUserStatus(rs.getInt(7));
				userBean.setRegisterTime(rs.getLong(8));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userBean, sql, globalUserId);
		return userBean;
	}

	// userIdPubk -> Full Profile
	public UserProfileBean queryUserProfileByPubk(String userIdPubk) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT site_user_id,site_login_id,user_id_pubk,user_name,user_photo,user_status,self_introduce,register_time FROM "
				+ USER_PROFILE_TABLE + " WHERE user_id_pubk=?;";

		UserProfileBean userBean = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, userIdPubk);

			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new UserProfileBean();
				userBean.setSiteUserId(rs.getString(1));
				userBean.setSiteLoginId(rs.getString(2));
				userBean.setUserIdPubk(rs.getString(3));
				userBean.setUserName(rs.getString(4));
				userBean.setUserPhoto(rs.getString(5));
				userBean.setUserStatus(rs.getInt(6));
				userBean.setSelfIntroduce(rs.getString(7));
				userBean.setRegisterTime(rs.getLong(8));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userBean, sql, userIdPubk);
		return userBean;
	}

	// Update Profile
	public int updateUserProfile(UserProfileBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_PROFILE_TABLE + " {} WHERE site_user_id=?;";

		Map<String, String> sqlMap = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(bean.getSiteLoginId())) {
			sqlMap.put("site_login_id", bean.getSiteLoginId());// 站点账号
			sqlMap.put("login_id_lowercase", bean.getSiteLoginId().toLowerCase());// 用户昵称拼音
		}
		sqlMap.put("user_name", bean.getUserName());// 用户昵称nickname
		sqlMap.put("user_name_in_latin", bean.getUserNameInLatin());// 用户昵称拼音
		sqlMap.put("user_photo", bean.getUserPhoto());// 用户头像
		sqlMap.put("self_introduce", bean.getSelfIntroduce());// 自我介绍

		SqlUtils.SqlBean sqlBean = SqlUtils.buildUpdateSql(sql, sqlMap);
		String realSql = sqlBean.getSql();

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(realSql);
			for (Integer index : sqlBean.getParams().keySet()) {
				ps.setString(index, sqlBean.getParams().get(index));
			}
			ps.setString(sqlBean.getParams().size() + 1, bean.getSiteUserId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, realSql, bean.getUserName(), bean.getUserPhoto(),
				bean.getSelfIntroduce(), bean.getSiteUserId());
		return result;
	}

	// Update User Status
	public int updateUserStatus(String siteUserId, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_PROFILE_TABLE + " SET user_status=? WHERE site_user_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setString(2, siteUserId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, status, siteUserId);
		return result;
	}

	/**
	 * 分页获取用户列表，这个列表包含用户与查询的用户之间的关系,扩展中使用
	 *
	 * @param siteUserId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public List<SimpleUserRelationBean> queryUserRelationPageList(String siteUserId, int pageNum, int pageSize)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserRelationBean> userPageList = new ArrayList<SimpleUserRelationBean>();
		String sql = "SELECT a.site_user_id,a.user_name,a.user_name_in_latin,a.user_photo,a.user_status,b.site_friend_id from "
				+ USER_PROFILE_TABLE + " AS a LEFT JOIN (SELECT site_user_id,site_friend_id FROM "
				+ SQLConst.SITE_USER_FRIEND
				+ " WHERE site_user_id=?) AS b ON a.site_user_id=b.site_friend_id ORDER BY a.id DESC LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);
			pst.setInt(2, startNum);
			pst.setInt(3, pageSize);

			rs = pst.executeQuery();
			while (rs.next()) {
				SimpleUserRelationBean bean = new SimpleUserRelationBean();
				bean.setUserId(rs.getString(1));
				bean.setUserName(rs.getString(2));
				bean.setUserNameInLatin(rs.getString(3));
				bean.setUserPhoto(rs.getString(4));
				bean.setUserStatus(rs.getInt(5));
				if (StringUtils.isNotBlank(rs.getString(6))) {
					bean.setRelation(UserProto.UserRelation.RELATION_FRIEND_VALUE);
				} else {
					bean.setRelation(UserProto.UserRelation.RELATION_NONE_VALUE);
				}
				userPageList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userPageList.size(), sql, startNum, pageSize);
		return userPageList;
	}

	public int queryTotalUserNum() throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT COUNT(*) FROM " + USER_PROFILE_TABLE + ";";

		int userNum = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			if (rs.next()) {
				userNum = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userNum, sql);
		return userNum;

	}

	/**
	 * 分页获取站点上所有用户
	 *
	 * @param pageNum
	 *            第几页，从1开始
	 * @param pageSize
	 *            每页大小
	 * @return
	 * @throws SQLException
	 */
	public List<SimpleUserBean> queryUserPageList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userPageList = new ArrayList<SimpleUserBean>();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ "  ORDER BY id DESC LIMIT ?,?;";
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
				SimpleUserBean bean = new SimpleUserBean();
				bean.setUserId(rs.getString(1));
				bean.setUserName(rs.getString(2));
				bean.setUserPhoto(rs.getString(3));
				bean.setUserStatus(rs.getInt(4));
				userPageList.add(bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userPageList.size(), sql, startNum, pageSize);
		return userPageList;
	}

	// siteUserId -> Mute
	public boolean queryMute(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		boolean mute = false;
		String sql = "SELECT mute FROM " + USER_PROFILE_TABLE + " WHERE site_user_id=?;";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, siteUserId);

			rs = pst.executeQuery();
			if (rs.next()) {
				mute = rs.getBoolean(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, mute, sql, siteUserId);
		return mute;
	}

	// Update Mute
	public boolean updateMute(String siteUserId, boolean mute) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + USER_PROFILE_TABLE + " SET mute=? WHERE site_user_id=?;";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setBoolean(1, mute);
			ps.setString(2, siteUserId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, mute, siteUserId);
		return result > 0;
	}

	public int queryRegisterNumPerDay(long now, int day) throws SQLException {
		long startTime = System.currentTimeMillis();
		long startTimeOfDay = TimeFormats.getStartTimeOfDay(now);
		long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
		if (day != 0) {
			startTimeOfDay = startTimeOfDay - TimeUnit.DAYS.toMillis(day);
			endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
		}
		String sql = "SELECT COUNT(*) FROM " + USER_PROFILE_TABLE + " WHERE register_time BETWEEN ? and ?;";

		int count = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, startTimeOfDay);
			pst.setLong(2, endTimeOfDay);

			rs = pst.executeQuery();
			count = rs.getInt(1);
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}
		LogUtils.dbDebugLog(logger, startTime, count, sql);
		return count;
	}

	public int getUserNum(long now, int day) throws SQLException {
		long startTime = System.currentTimeMillis();
		long endTimeOfDay = TimeFormats.getEndTimeOfDay(now);
		if (day != 0) {
			endTimeOfDay = endTimeOfDay - TimeUnit.DAYS.toMillis(day);
		}
		String sql = "SELECT COUNT(*) FROM " + USER_PROFILE_TABLE + " WHERE register_time < ?;";

		int userNum = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DatabaseConnection.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, endTimeOfDay);

			rs = pst.executeQuery();
			userNum = rs.getInt(1);
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, pst, rs);
		}

		LogUtils.dbDebugLog(logger, startTime, userNum, sql);
		return userNum;

	}

	public boolean delUser(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + USER_PROFILE_TABLE + " WHERE site_user_id=? ";

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseConnection.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, siteUserId);

			result = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			DatabaseConnection.returnConnection(conn, ps);
		}

		LogUtils.dbDebugLog(logger, startTime, result, sql, siteUserId);
		return result > 0;
	}
}