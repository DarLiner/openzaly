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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.UserProto;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 用户个人资料表(db_table:site_user_profile)相关操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-09 20:19:59
 */
public class SQLiteUserProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUserProfileDao.class);
	private final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;
	private final String USER_FRIEND_TABLE = SQLConst.SITE_USER_FRIEND;
	private static SQLiteUserProfileDao instance = new SQLiteUserProfileDao();

	public static SQLiteUserProfileDao getInstance() {
		return instance;
	}

	public boolean saveUserProfile(UserProfileBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + USER_PROFILE_TABLE
				+ "(site_user_id,user_id_pubk,user_name,user_photo,phone_id,user_status,self_introduce,apply_info,register_time, global_user_id) VALUES(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getSiteUserId());
		preStatement.setString(2, bean.getUserIdPubk());
		preStatement.setString(3, bean.getUserName());
		preStatement.setString(4, bean.getUserPhoto());
		preStatement.setString(5, bean.getPhoneId());
		preStatement.setInt(6, bean.getUserStatus());
		preStatement.setString(7, bean.getSelfIntroduce());
		preStatement.setString(8, bean.getApplyInfo());
		preStatement.setLong(9, bean.getRegisterTime());
		preStatement.setString(10, bean.getGlobalUserId());

		int result = preStatement.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "", sql + bean.toString());

		if (result == 1) {
			return true;
		}
		return false;
	}

	public String querySiteUserId(String userIdPubk) throws SQLException {
		long startTime = System.currentTimeMillis();
		String siteUserId = null;
		String sql = "SELECT site_user_id FROM " + SQLConst.SITE_USER_PROFILE + " WHERE user_id_pubk=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userIdPubk);

		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			siteUserId = rs.getString(1);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, siteUserId, sql + userIdPubk);
		return siteUserId;
	}

	public SimpleUserBean querySimpleProfileByGlobalUserId(String globalUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		SimpleUserBean userBean = new SimpleUserBean();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE global_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, globalUserId);

		ResultSet rs = preStatement.executeQuery();

		if (rs.next()) {
			userBean.setUserId(rs.getString(1));
			userBean.setUserName(rs.getString(2));
			userBean.setUserPhoto(rs.getString(3));
			userBean.setUserStatus(rs.getInt(4));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userBean.toString(), sql + "," + globalUserId);
		return userBean;
	}

	public SimpleUserBean querySimpleProfileById(String userId) throws SQLException {
		long startTime = System.currentTimeMillis();
		SimpleUserBean userBean = new SimpleUserBean();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userId);

		ResultSet rs = preStatement.executeQuery();

		if (rs.next()) {
			userBean.setUserId(rs.getString(1));
			userBean.setUserName(rs.getString(2));
			userBean.setUserPhoto(rs.getString(3));
			userBean.setUserStatus(rs.getInt(4));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userBean.toString(), sql + "," + userId);
		return userBean;
	}

	public SimpleUserBean querySimpleProfileByPubk(String userIdPubk) throws SQLException {
		long startTime = System.currentTimeMillis();
		SimpleUserBean userBean = new SimpleUserBean();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE user_id_pubk=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userIdPubk);

		ResultSet rs = preStatement.executeQuery();

		if (rs.next()) {
			userBean.setUserId(rs.getString(1));
			userBean.setUserName(rs.getString(2));
			userBean.setUserPhoto(rs.getString(3));
			userBean.setUserStatus(rs.getInt(4));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userBean.toString(), sql + "," + userIdPubk);
		return userBean;
	}

	public List<SimpleUserBean> querySimpleProfileByName(String userName) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userList = new ArrayList<SimpleUserBean>();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ " WHERE user_name LIKE ?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, "%" + userName + "%");

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			SimpleUserBean userBean = new SimpleUserBean();
			userBean.setUserId(rs.getString(1));
			userBean.setUserName(rs.getString(2));
			userBean.setUserPhoto(rs.getString(3));
			userBean.setUserStatus(rs.getInt(4));
			userList.add(userBean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userList.toString(), sql + "," + userName);
		return userList;
	}

	/**
	 * 通过站点用户ID，查询用户
	 * 
	 * @param siteUserId
	 * @return
	 * @throws SQLException
	 */
	public UserProfileBean queryUserProfileById(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		UserProfileBean userBean = new UserProfileBean();
		String sql = "SELECT site_user_id,user_id_pubk,user_name,user_photo,self_introduce,user_status,register_time FROM "
				+ USER_PROFILE_TABLE + " WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);

		ResultSet rs = preStatement.executeQuery();

		if (rs.next()) {
			userBean.setSiteUserId(rs.getString(1));
			userBean.setUserIdPubk(rs.getString(2));
			userBean.setUserName(rs.getString(3));
			userBean.setUserPhoto(rs.getString(4));
			userBean.setSelfIntroduce(rs.getString(5));
			userBean.setUserStatus(rs.getInt(6));
			userBean.setRegisterTime(rs.getLong(7));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userBean.toString(), sql + "," + siteUserId);
		return userBean;
	}

	public String queryGlobalUserId(String siteUserId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String globalUserId = null;
		String sql = "SELECT global_user_id FROM " + USER_PROFILE_TABLE + " WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);

		ResultSet rs = preStatement.executeQuery();

		if (rs.next()) {
			globalUserId = rs.getString(1);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, globalUserId, sql + "," + siteUserId);
		return globalUserId;
	}

	/**
	 * 通过globalUserId查询用户信息
	 * 
	 * @param id
	 *            globalUserId
	 * @return
	 * @throws SQLException
	 */
	public UserProfileBean queryUserProfileByGlobalUserId(String id) throws SQLException {
		long startTime = System.currentTimeMillis();
		UserProfileBean userBean = new UserProfileBean();
		String sql = "SELECT site_user_id,user_id_pubk,user_name,user_photo,self_introduce,user_status,register_time FROM "
				+ USER_PROFILE_TABLE + " WHERE global_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, id);

		ResultSet rs = preStatement.executeQuery();

		if (rs.next()) {
			userBean.setSiteUserId(rs.getString(1));
			userBean.setUserIdPubk(rs.getString(2));
			userBean.setUserName(rs.getString(3));
			userBean.setUserPhoto(rs.getString(4));
			userBean.setSelfIntroduce(rs.getString(5));
			userBean.setUserStatus(rs.getInt(6));
			userBean.setRegisterTime(rs.getLong(7));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userBean.toString(), sql + "," + id);
		return userBean;
	}

	public UserProfileBean queryUserProfileByPubk(String userIdPubk) throws SQLException {
		long startTime = System.currentTimeMillis();
		UserProfileBean userBean = new UserProfileBean();
		String sql = "SELECT site_user_id,user_id_pubk,user_name,user_photo,user_status,self_introduce,register_time FROM "
				+ USER_PROFILE_TABLE + " WHERE user_id_pubk=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userIdPubk);

		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			userBean.setSiteUserId(rs.getString(1));
			userBean.setUserIdPubk(rs.getString(2));
			userBean.setUserName(rs.getString(3));
			userBean.setUserPhoto(rs.getString(4));
			userBean.setUserStatus(rs.getInt(5));
			userBean.setSelfIntroduce(rs.getString(6));
			userBean.setRegisterTime(rs.getLong(7));
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userBean.toString(), sql + "," + userIdPubk);
		return userBean;
	}

	public int updateUserProfile(UserProfileBean userBean) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_PROFILE_TABLE
				+ " SET user_name=?,user_photo=?,self_introduce=? WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userBean.getUserName());
		preStatement.setString(2, userBean.getUserPhoto());
		preStatement.setString(3, userBean.getSelfIntroduce());
		preStatement.setString(4, userBean.getSiteUserId());
		result = preStatement.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "", sql + userBean.toString());
		return result;
	}

	/**
	 * <pre>
	 * 更新用户的个人状态
	 * </pre>
	 * 
	 * @param siteUserId
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public int updateUserStatus(String siteUserId, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		int result = 0;
		String sql = "UPDATE " + USER_PROFILE_TABLE + " SET user_status=? WHERE site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, status);
		preStatement.setString(2, siteUserId);
		result = preStatement.executeUpdate();

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result + "", sql + siteUserId + "," + status);

		return result;
	}

	public List<SimpleUserBean> queryUserFriends(String userId) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userFriendList = new ArrayList<SimpleUserBean>();

		String sql = "SELECT a.site_friend_id,b.user_name,b.user_photo FROM " + USER_FRIEND_TABLE + " AS a LEFT JOIN "
				+ USER_PROFILE_TABLE + " AS b WHERE a.site_friend_id=b.site_user_id AND a.site_user_id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, userId);

		ResultSet rs = preStatement.executeQuery();

		while (rs.next()) {
			SimpleUserBean bean = new SimpleUserBean();
			bean.setUserId(rs.getString(1));
			bean.setUserName(rs.getString(2));
			bean.setUserPhoto(rs.getString(3));
			userFriendList.add(bean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userFriendList.toString(), sql + userId + "," + userId);

		return userFriendList;
	}

	/**
	 * 分页获取用户列表，这个列表包含用户与查询的用户之前的关系
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
		String sql = "SELECT a.site_user_id,a.user_name,a.user_photo,a.user_status,b.site_friend_id from "
				+ USER_PROFILE_TABLE + " AS a LEFT JOIN (SELECT site_user_id,site_friend_id FROM "
				+ SQLConst.SITE_USER_FRIEND
				+ " WHERE site_user_id=?) AS b ON a.site_user_id=b.site_friend_id ORDER BY a.id DESC LIMIT ?,?;";
		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, siteUserId);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			SimpleUserRelationBean bean = new SimpleUserRelationBean();
			bean.setUserId(rs.getString(1));
			bean.setUserName(rs.getString(2));
			bean.setUserPhoto(rs.getString(3));
			bean.setUserStatus(rs.getInt(4));
			if (StringUtils.isNotBlank(rs.getString(5))) {
				bean.setRelation(UserProto.UserRelation.RELATION_FRIEND_VALUE);
			} else {
				bean.setRelation(UserProto.UserRelation.RELATION_NONE_VALUE);
			}
			userPageList.add(bean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userPageList.toString(), sql + startNum + "," + pageSize);

		return userPageList;
	}

	/**
	 * 单独获取当前站点的用户列表
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public List<SimpleUserBean> queryUserPageList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<SimpleUserBean> userPageList = new ArrayList<SimpleUserBean>();
		String sql = "SELECT site_user_id,user_name,user_photo,user_status FROM " + USER_PROFILE_TABLE
				+ "  ORDER BY id DESC LIMIT ?,?;";
		int startNum = (pageNum - 1) * pageSize;
		int endNum = pageNum * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, startNum);
		preStatement.setInt(2, endNum);

		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			SimpleUserBean bean = new SimpleUserBean();
			bean.setUserId(rs.getString(1));
			bean.setUserName(rs.getString(2));
			bean.setUserPhoto(rs.getString(3));
			bean.setUserStatus(rs.getInt(4));
			userPageList.add(bean);
		}

		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, userPageList.toString(), sql + startNum + "," + endNum);

		return userPageList;
	}

}