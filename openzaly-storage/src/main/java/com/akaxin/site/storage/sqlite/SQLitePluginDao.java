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
import com.akaxin.common.utils.StringHelper;
import com.akaxin.site.storage.bean.PluginBean;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

/**
 * 站点扩展表相关数据操作
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-11 17:26:52
 */
public class SQLitePluginDao {
	private static final Logger logger = LoggerFactory.getLogger(SQLitePluginDao.class);
	private final String PLUGIN_TABLE = SQLConst.SITE_PLUGIN_MANAGER;

	public static SQLitePluginDao getInstance() {
		return SingletonHolder.instance;
	}

	public String reSetAuthKey(int pluginId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String newAuthKey = StringHelper.generateRandomString(16);
		String sql = "UPDATE " + PLUGIN_TABLE + " SET auth_key = ? WHERE id = ?";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, newAuthKey);
		preStatement.setInt(2, pluginId);
		int i = preStatement.executeUpdate();
		if (i > 0) {
			return newAuthKey;
		}
		return "false";
	}

	static class SingletonHolder {
		private static SQLitePluginDao instance = new SQLitePluginDao();
	}

	public boolean addPlugin(PluginBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + PLUGIN_TABLE//
				+ "(name," //
				+ "icon," //
				+ "url_page,"//
				+ "api_url,"//
				+ "auth_key,"//
				+ "allowed_ip,"//
				+ "position,"//
				+ "sort,"//
				+ "display_mode,"//
				+ "permission_status,"//
				+ "add_time) VALUES(?,?,?,?,?,?,?,?,?,?,?);";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getName());
		preStatement.setString(2, bean.getIcon());
		preStatement.setString(3, bean.getUrlPage());
		preStatement.setString(4, bean.getApiUrl());
		preStatement.setString(5, bean.getAuthKey());
		preStatement.setString(6, bean.getAllowedIp());
		preStatement.setInt(7, bean.getPosition());
		preStatement.setInt(8, bean.getSort());
		preStatement.setInt(9, bean.getDisplayMode());
		preStatement.setInt(10, bean.getPermissionStatus());
		preStatement.setLong(11, bean.getAddTime());
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getName(), bean.getIcon(), bean.getUrlPage(),
				bean.getApiUrl(), bean.getAuthKey(), bean.getAllowedIp(), bean.getPosition(), bean.getSort(),
				bean.getDisplayMode(), bean.getPermissionStatus(), bean.getAddTime());
		return result > 0;
	}

	public boolean updatePlugin(PluginBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + PLUGIN_TABLE + " SET "//
				+ "name=?,"//
				+ "icon=?,"//
				+ "url_page=?,"//
				+ "api_url=?,"//
				+ "allowed_ip=?,"//
				+ "position=?,"//
				+ "sort=?,"//
				+ "display_mode=?,"//
				+ "permission_status=? "//
				+ "WHERE id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getName());
		preStatement.setString(2, bean.getIcon());
		preStatement.setString(3, bean.getUrlPage());
		preStatement.setString(4, bean.getApiUrl());
		preStatement.setString(5, bean.getAllowedIp());
		preStatement.setInt(6, bean.getPosition());
		preStatement.setInt(7, bean.getSort());
		preStatement.setInt(8, bean.getDisplayMode());
		preStatement.setInt(9, bean.getPermissionStatus());
		preStatement.setInt(10, bean.getId());
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, bean.getName(), bean.getIcon(), bean.getUrlPage(),
				bean.getApiUrl(), bean.getAllowedIp(), bean.getPosition(), bean.getSort(), bean.getDisplayMode(),
				bean.getPermissionStatus(), bean.getId());
		return result > 0;
	}

	public boolean deletePlugin(int pluginId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + PLUGIN_TABLE + " WHERE id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, pluginId);
		int result = preStatement.executeUpdate();

		LogUtils.dbDebugLog(logger, startTime, result, sql, pluginId);
		return result > 0;
	}

	public PluginBean queryPluginProfile(int pluginId) throws SQLException {
		long startTime = System.currentTimeMillis();
		PluginBean pluginBean = new PluginBean();
		String sql = "SELECT id," //
				+ "name,"//
				+ "icon,"//
				+ "url_page,"//
				+ "api_url,"//
				+ "auth_key,"//
				+ "allowed_ip,"//
				+ "position,"//
				+ "sort,"//
				+ "display_mode,"//
				+ "permission_status"//
				+ " FROM " + PLUGIN_TABLE + " WHERE id=?;";

		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, pluginId);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			pluginBean.setId(rs.getInt(1));
			pluginBean.setName(rs.getString(2));
			pluginBean.setIcon(rs.getString(3));
			pluginBean.setUrlPage(rs.getString(4));
			pluginBean.setApiUrl(rs.getString(5));
			pluginBean.setAuthKey(rs.getString(6));
			pluginBean.setAllowedIp(rs.getString(7));
			pluginBean.setPosition(rs.getInt(8));
			pluginBean.setSort(rs.getInt(9));
			pluginBean.setDisplayMode(rs.getInt(10));
			pluginBean.setPermissionStatus(rs.getInt(11));
		}

		LogUtils.dbDebugLog(logger, startTime, pluginBean.toString(), sql, pluginId);
		return pluginBean;
	}

	/**
	 * 按照位置和权限，分页查询
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param position
	 * @param permissionStatus
	 * @return
	 * @throws SQLException
	 */
	public List<PluginBean> queryPluginList(int pageNum, int pageSize, int position, int permissionStatus)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		List<PluginBean> pluginList = new ArrayList<PluginBean>();
		String sql = "SELECT id," //
				+ "name,"//
				+ "icon,"//
				+ "url_page,"//
				+ "api_url,"//
				+ "auth_key,"//
				+ "sort,"//
				+ "position,"//
				+ "display_mode,"//
				+ "permission_status"//
				+ " FROM " + PLUGIN_TABLE + " WHERE "//
				+ "position=? AND " + "permission_status=? " + "ORDER BY sort LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, position);
		preStatement.setInt(2, permissionStatus);
		preStatement.setInt(3, startNum);
		preStatement.setInt(4, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			PluginBean bean = new PluginBean();
			bean.setId(rs.getInt(1));
			bean.setName(rs.getString(2));
			bean.setIcon(rs.getString(3));
			bean.setUrlPage(rs.getString(4));
			bean.setApiUrl(rs.getString(5));
			bean.setAuthKey(rs.getString(6));
			bean.setSort(rs.getInt(7));
			bean.setPosition(rs.getInt(8));
			bean.setDisplayMode(rs.getInt(9));
			bean.setPermissionStatus(rs.getInt(10));
			pluginList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, pluginList.size(), sql, position, permissionStatus, startNum, pageSize);
		return pluginList;
	}

	/**
	 * 按照位置，分页查询
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param position
	 * @return
	 * @throws SQLException
	 */
	public List<PluginBean> queryPluginList(int pageNum, int pageSize, int position) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<PluginBean> pluginList = new ArrayList<PluginBean>();
		String sql = "SELECT id," //
				+ "name,"//
				+ "icon,"//
				+ "url_page,"//
				+ "api_url,"//
				+ "auth_key,"//
				+ "sort,"//
				+ "position,"//
				+ "display_mode,"// NewPage or FloatingPage
				+ "permission_status"//
				+ " FROM " + PLUGIN_TABLE + " WHERE "//
				+ "position=? ORDER BY sort LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, position);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			PluginBean bean = new PluginBean();
			bean.setId(rs.getInt(1));
			bean.setName(rs.getString(2));
			bean.setIcon(rs.getString(3));
			bean.setUrlPage(rs.getString(4));
			bean.setApiUrl(rs.getString(5));
			bean.setAuthKey(rs.getString(6));
			bean.setSort(rs.getInt(7));
			bean.setPosition(rs.getInt(8));
			bean.setDisplayMode(rs.getInt(9));
			bean.setPermissionStatus(rs.getInt(10));
			pluginList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, pluginList.size(), sql, position, startNum, pageSize);
		return pluginList;
	}

	/**
	 * 分页查询所有的扩展，管理后台使用
	 *
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public List<PluginBean> queryAllPluginList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<PluginBean> pluginList = new ArrayList<PluginBean>();
		String sql = "SELECT id,"//
				+ "name,"//
				+ "icon,"//
				+ "url_page,"//
				+ "api_url,"//
				+ "sort,"//
				+ "position,"//
				+ "display_mode,"//
				+ "permission_status"//
				+ " FROM " + PLUGIN_TABLE + " ORDER BY sort LIMIT ?,?;";

		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, startNum);
		preStatement.setInt(2, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			PluginBean bean = new PluginBean();
			bean.setId(rs.getInt(1));
			bean.setName(rs.getString(2));
			bean.setIcon(rs.getString(3));
			bean.setUrlPage(rs.getString(4));
			bean.setApiUrl(rs.getString(5));
			bean.setSort(rs.getInt(6));
			bean.setPosition(rs.getInt(7));
			bean.setDisplayMode(rs.getInt(8));
			bean.setPermissionStatus(rs.getInt(9));
			pluginList.add(bean);
		}

		LogUtils.dbDebugLog(logger, startTime, pluginList.size(), sql, startNum, pageSize);
		return pluginList;
	}
}
