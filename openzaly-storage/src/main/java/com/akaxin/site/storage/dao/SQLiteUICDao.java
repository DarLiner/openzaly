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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;

/**
 * 用户邀请码
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-11 17:26:52
 */
public class SQLiteUICDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUICDao.class);
	private final String UIC_TABLE = SQLConst.SITE_USER_UIC;
	private final String USER_PROFILE_TABLE = SQLConst.SITE_USER_PROFILE;

	public static SQLiteUICDao getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static SQLiteUICDao instance = new SQLiteUICDao();
	}

	/**
	 * 新增UIC码
	 * 
	 * @param bean
	 * @return
	 * @throws SQLException
	 */
	public boolean addUIC(UicBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + UIC_TABLE + "(uic,status,create_time) VALUES(?,?,?);";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getUic());
		preStatement.setInt(2, bean.getStatus());
		long currentTime = System.currentTimeMillis();
		preStatement.setLong(3, currentTime);
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getUic(), bean.getStatus(), currentTime);
		return result > 0;
	}

	/**
	 * 批量新增UIC
	 * 
	 * @param bean
	 * @param num
	 * @return
	 * @throws SQLException
	 */
	public boolean batchAddUIC(UicBean bean, int num, int length) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + UIC_TABLE + "(uic,status,create_time) VALUES(?,?,?);";
		int successCount = 0;
		length = length < 6 ? 6 : length;// 最短6位
		try {
			SQLiteJDBCManager.getConnection().setAutoCommit(false);
			for (int i = 0; i < num; i++) {
				try {
					// int uic = (int) ((Math.random() * 9 + 1) * 100000);
					String uicValue = StringHelper.generateRandomNumber(length);
					PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
					preStatement.setString(1, uicValue);
					preStatement.setInt(2, bean.getStatus());
					preStatement.setLong(3, System.currentTimeMillis());
					preStatement.executeUpdate();
					successCount++;
				} catch (Exception e) {
					logger.error("execute uic sql error ", e);
				}
			}
			SQLiteJDBCManager.getConnection().commit();
			SQLiteJDBCManager.getConnection().setAutoCommit(true);
		} catch (Exception e) {
			SQLiteJDBCManager.getConnection().rollback();
			logger.error(StringHelper.format("batch add uic error bean={} num={}", bean.toString(), num), e);
		}

		LogUtils.dbDebugLog(logger, startTime, successCount, sql, "randomUic", bean.getStatus(),
				System.currentTimeMillis());
		return successCount > 0;
	}

	/**
	 * 查询UIC使用情况
	 * 
	 * @param uic
	 * @return
	 * @throws SQLException
	 */
	public UicBean queryUIC(String uic) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "SELECT uic,site_user_id,status,create_time,use_time FROM " + UIC_TABLE + " WHERE uic=?;";

		UicBean bean = null;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, uic);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			bean = new UicBean();
			bean.setUic(rs.getString(1));
			bean.setSiteUserId(rs.getString(2));
			bean.setStatus(rs.getInt(3));
			bean.setCreateTime(rs.getLong(4));
			bean.setUseTime(rs.getLong(5));
		}

		LogUtils.dbDebugLog(logger, startTime, bean, sql, uic);
		return bean;
	}

	/**
	 * 更新用户使用的UIC
	 * 
	 * @param bean
	 * @return
	 * @throws SQLException
	 */
	public boolean updateUIC(UicBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + UIC_TABLE + " SET site_user_id=?,status=?,use_time=? WHERE uic=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getSiteUserId());
		preStatement.setInt(2, bean.getStatus());
		long currentTime = System.currentTimeMillis();
		preStatement.setLong(3, currentTime);
		preStatement.setString(4, bean.getUic());
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getSiteUserId(), bean.getStatus(), currentTime,
				bean.getUic());
		return result > 0;
	}

	public List<UicBean> queryUicList(int pageNum, int pageSize, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<UicBean> uicList = new ArrayList<UicBean>();
		String sql = "SELECT a.id,a.uic,a.site_user_id,b.user_name,a.create_time,a.use_time FROM " + UIC_TABLE
				+ " AS a LEFT JOIN " + USER_PROFILE_TABLE
				+ " AS b ON a.site_user_id=b.site_user_id where a.status=? LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, status);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			UicBean bean = new UicBean();
			bean.setId(rs.getInt(1));
			bean.setUic(rs.getString(2));
			bean.setSiteUserId(rs.getString(3));
			bean.setUserName(rs.getString(4));
			bean.setCreateTime(rs.getLong(5));
			bean.setUseTime(rs.getLong(6));
			uicList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, uicList, sql);
		return uicList;
	}

	/**
	 * 查询所有UIC列表
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public List<UicBean> queryAllUicList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<UicBean> uicList = new ArrayList<UicBean>();
		String sql = "SELECT a.id,a.uic,a.site_user_id,b.user_name,a.create_time,a.use_time FROM " + UIC_TABLE
				+ " AS a LEFT JOIN " + USER_PROFILE_TABLE + " AS b ON a.site_user_id=b.site_user_id LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, startNum);
		preStatement.setInt(2, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			UicBean bean = new UicBean();
			bean.setId(rs.getInt(1));
			bean.setUic(rs.getString(2));
			bean.setSiteUserId(rs.getString(3));
			bean.setUserName(rs.getString(4));
			bean.setCreateTime(rs.getLong(5));
			bean.setUseTime(rs.getLong(6));
			uicList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, uicList, sql, startNum, pageSize);
		return uicList;
	}
}
