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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.logs.LogUtils;
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

	static class SingletonHolder {
		private static SQLitePluginDao instance = new SQLitePluginDao();
	}

	public boolean addPlugin(PluginBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "INSERT INTO " + PLUGIN_TABLE
				+ "(name,icon,url_page,url_api,auth_key,allowed_ip,status,sort,add_time) VALUES(?,?,?,?,?,?,0,?,?);";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getName());
		preStatement.setString(2, bean.getIcon());
		preStatement.setString(3, bean.getUrlPage());
		preStatement.setString(4, bean.getUrlApi());
		preStatement.setString(5, bean.getAuthKey());
		preStatement.setString(6, bean.getUrlApi());
		preStatement.setInt(7, bean.getSort());
		preStatement.setLong(8, bean.getAddTime());
		int result = preStatement.executeUpdate();
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + bean.toString());
		return result > 0;
	}

	public boolean updatePlugin(PluginBean bean) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + PLUGIN_TABLE
				+ " SET name=?,icon=?,url_page=?,url_api=?,auth_key=?,allowed_ip=?,status=?,sort=? WHERE id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setString(1, bean.getName());
		preStatement.setString(2, bean.getIcon());
		preStatement.setString(3, bean.getUrlPage());
		preStatement.setString(4, bean.getUrlApi());
		preStatement.setString(5, bean.getAuthKey());
		preStatement.setString(6, bean.getUrlApi());
		preStatement.setInt(7, bean.getStatus());
		preStatement.setInt(8, bean.getSort());
		preStatement.setInt(9, bean.getId());
		int result = preStatement.executeUpdate();
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + bean.toString());
		return result > 0;
	}

	public boolean updatePluginStatus(int pluginId, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "UPDATE " + PLUGIN_TABLE + " SET status=? WHERE id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, status);
		preStatement.setInt(2, pluginId);
		int result = preStatement.executeUpdate();
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + pluginId + "," + status);
		return result > 0;
	}

	public boolean deletePlugin(int pluginId) throws SQLException {
		long startTime = System.currentTimeMillis();
		String sql = "DELETE FROM " + PLUGIN_TABLE + " WHERE id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, pluginId);
		int result = preStatement.executeUpdate();
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, result, sql + pluginId);
		return result > 0;
	}

	public PluginBean queryPluginProfile(int pluginId) throws SQLException {
		long startTime = System.currentTimeMillis();
		PluginBean pluginBean = new PluginBean();
		String sql = "SELECT id,name,icon,url_page,url_api,auth_key,allowed_ip,status FROM " + PLUGIN_TABLE
				+ " WHERE id=?;";
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, pluginId);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			pluginBean.setId(rs.getInt(1));
			pluginBean.setName(rs.getString(2));
			pluginBean.setIcon(rs.getString(3));
			pluginBean.setUrlPage(rs.getString(4));
			pluginBean.setUrlApi(rs.getString(5));
			pluginBean.setAuthKey(rs.getString(6));
			pluginBean.setAllowedIp(rs.getString(7));
			pluginBean.setStatus(rs.getInt(8));
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, pluginBean.toString(), sql + pluginId);
		return pluginBean;
	}

	public List<PluginBean> queryPluginList(int pageNum, int pageSize, int status) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<PluginBean> pluginList = new ArrayList<PluginBean>();
		String sql = "SELECT id,name,icon,url_page,url_api,status FROM " + PLUGIN_TABLE
				+ " WHERE status=? ORDER BY sort LIMIT ?,?;";
		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, status);
		preStatement.setInt(2, startNum);
		preStatement.setInt(3, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			PluginBean bean = new PluginBean();
			bean.setId(rs.getInt(1));
			bean.setName(rs.getString(2));
			bean.setIcon(rs.getString(3));
			bean.setUrlPage(rs.getString(4));
			bean.setUrlApi(rs.getString(5));
			bean.setStatus(rs.getInt(6));
			pluginList.add(bean);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, pluginList, sql);
		return pluginList;
	}

	public List<PluginBean> queryPluginList(int pageNum, int pageSize, int status1, int status2) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<PluginBean> pluginList = new ArrayList<PluginBean>();
		String sql = "SELECT id,name,icon,url_page,url_api,status FROM " + PLUGIN_TABLE
				+ " WHERE status=? OR status=? ORDER BY sort LIMIT ?,?;";
		int startNum = (pageNum - 1) * pageSize;
		PreparedStatement preStatement = SQLiteJDBCManager.getConnection().prepareStatement(sql);
		preStatement.setInt(1, status1);
		preStatement.setInt(2, status2);
		preStatement.setInt(3, startNum);
		preStatement.setInt(4, pageSize);
		ResultSet rs = preStatement.executeQuery();
		while (rs.next()) {
			PluginBean bean = new PluginBean();
			bean.setId(rs.getInt(1));
			bean.setName(rs.getString(2));
			bean.setIcon(rs.getString(3));
			bean.setUrlPage(rs.getString(4));
			bean.setUrlApi(rs.getString(5));
			bean.setStatus(rs.getInt(6));
			pluginList.add(bean);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, pluginList, sql);
		return pluginList;
	}

	public List<PluginBean> queryAllPluginList(int pageNum, int pageSize) throws SQLException {
		long startTime = System.currentTimeMillis();
		List<PluginBean> pluginList = new ArrayList<PluginBean>();
		String sql = "SELECT id,name,icon,url_page,url_api,status FROM " + PLUGIN_TABLE + " ORDER BY sort LIMIT ?,?;";
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
			bean.setUrlApi(rs.getString(5));
			bean.setStatus(rs.getInt(6));
			pluginList.add(bean);
		}
		long endTime = System.currentTimeMillis();
		LogUtils.printDBLog(logger, endTime - startTime, pluginList, sql);
		return pluginList;
	}
}
