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
package com.akaxin.site.storage.sqlite.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.core.UicProto;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.storage.sqlite.SQLiteSiteConfigDao;
import com.akaxin.site.storage.sqlite.SQLiteUICDao;
import com.akaxin.site.storage.sqlite.sql.SQLConst;
import com.akaxin.site.storage.sqlite.sql.SQLIndex;

/**
 * <pre>
 * SQLite数据源连接管理
 * 		1.数据源加载
 * 		2.检测数据库中的表
 * 		3.初始化站点设置信息
 * 		4.添加后台管理扩展
 * 		5.设置初始管理员邀请码UIC
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.22 11:59:38
 * 
 */
public class SQLiteJDBCManager {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteJDBCManager.class);

	private static String sqliteDriverName = "org.sqlite.JDBC";
	private static Connection sqlitConnection = null;
	private static final String checkTableSql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=? AND tbl_name=?;";
	private static final String DB_FILE_PATH = "openzalyDB.sqlite3";

	private SQLiteJDBCManager() {

	}

	public static void initSqliteDB(String dbDir) {
		loadDatabaseDriver(dbDir);
		checkDatabaseTable();
		checkDatabaseIndex();
	}

	public static void initSqliteDB(DBConfigBean bean) {
		loadDatabaseDriver(bean.getDbDir());
		checkDatabaseTable();
		checkDatabaseIndex();
		initSiteConfig(bean.getConfigMap());
		addSiteManagerPlugin(bean.getAdminServerName(), bean.getSiteServer(), bean.getAdminApi(), bean.getAdminIcon());
		initAdminUic(bean.getAdminUic());
	}

	private static void loadDatabaseDriver(String dbDir) {
		try {
			Class.forName(sqliteDriverName);
			String dbUrl = "jdbc:sqlite:";
			if (StringUtils.isNotEmpty(dbDir)) {
				if (dbDir.endsWith("/")) {
					dbUrl += dbDir + DB_FILE_PATH;
				} else {
					dbUrl += dbDir + "/" + DB_FILE_PATH;
				}
			} else {
				dbUrl += "./" + DB_FILE_PATH;
			}
			logger.info("load data base connectionUrl={}", dbUrl);
			sqlitConnection = DriverManager.getConnection(dbUrl);
		} catch (ClassNotFoundException e) {
			logger.error("class not found.", e);
		} catch (SQLException e) {
			logger.error("load sqlite driver error.", e);
		}
	}

	public static void initSiteConfig(Map<Integer, String> configMap) {
		try {
			Map<Integer, String> oldMap = SQLiteSiteConfigDao.getInstance().querySiteConfig();
			if (oldMap != null) {
				if (oldMap.get(ConfigProto.ConfigKey.SITE_ADMIN_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.SITE_ADMIN_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.PIC_PATH_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.PIC_PATH_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.REGISTER_WAY_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.REGISTER_WAY_VALUE);
				}
			}
			SQLiteSiteConfigDao.getInstance().updateSiteConfig(configMap);
		} catch (SQLException e) {
			logger.error("init site config error.");
		}
	}

	private static void checkDatabaseTable() {
		for (String tableName : SQLConst.SITE_TABLES_MAP.keySet()) {
			boolean result = createTable(tableName, SQLConst.SITE_TABLES_MAP.get(tableName));
			logger.info("create table:{} result={}", tableName, result);
		}
	}

	private static void checkDatabaseIndex() {
		for (String indexSql : SQLIndex.DB_INDEXS_SQL) {
			boolean result = createIndex(indexSql);
			logger.info("create index result:{} sql:{}", result, indexSql);
		}
	}

	private static boolean existTable(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return false;
		}
		try {
			PreparedStatement pst = sqlitConnection.prepareStatement(checkTableSql);
			pst.setString(1, tableName);
			pst.setString(2, tableName);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) >= 1;
			}
		} catch (SQLException e) {
			logger.error("check table exist error.", e);

		}
		return false;
	}

	private static boolean createTable(String tableName, String createTableSQL) {
		boolean result = false;
		try {
			PreparedStatement pst = sqlitConnection.prepareStatement(createTableSQL);
			pst.executeUpdate();
			// 再次检测是否创建成功
			if (existTable(tableName)) {
				return true;
			}
		} catch (Exception e) {
			logger.error("create table " + tableName + " sql=" + createTableSQL + " error.,", e);
		}
		return result;
	}

	private static boolean createIndex(String indexSQL) {
		boolean result = false;
		try {
			PreparedStatement pst = sqlitConnection.prepareStatement(indexSQL);
			pst.executeUpdate();
			result = true;
		} catch (Exception e) {
			logger.error("create index sql=" + indexSQL + " error.,", e);
		}
		return result;
	}

	private static void addSiteManagerPlugin(String siteName, String urlPage, String urlApi, String siteIcon) {
		boolean result = false;
		String updateSql = "UPDATE site_plugin_manager SET name=?,url_page=?,url_api=? WHERE id=1;";
		String insertSql = "INSERT INTO site_plugin_manager(id,name,icon,url_page,url_api,status) VALUES(1,?,?,?,?,?);";
		try {
			PreparedStatement pst = sqlitConnection.prepareStatement(updateSql);
			pst.setString(1, siteName);
			pst.setString(2, urlPage);
			pst.setString(3, urlApi);

			result = (pst.executeUpdate() > 0);
			logger.info("update site management result={} SQL={} name={} url_page={} url_api={}", result, updateSql,
					siteName, urlPage, urlApi);
		} catch (SQLException e) {
			logger.error("update site management error", e);
		}

		try {
			if (!result) {
				PreparedStatement pst = sqlitConnection.prepareStatement(insertSql);
				pst.setString(1, siteName);
				pst.setString(2, siteIcon);
				pst.setString(3, urlPage);
				pst.setString(4, urlApi);
				pst.setInt(5, PluginProto.PluginStatus.ADMIN_HOME_PAGE_SEE_VALUE);
				result = (pst.executeUpdate() > 0);
				logger.info("insert site management result={} SQL={} name={} url_page={} url_api={}", result, insertSql,
						siteName, urlPage, urlApi);
			}
		} catch (SQLException e) {
			logger.error("insert site management error", e);
		}
	}

	private static void initAdminUic(String uic) {
		boolean result = false;
		try {
			UicBean bean = new UicBean();
			bean.setUic(uic);
			bean.setStatus(UicProto.UicStatus.UNUSED_VALUE);
			bean.setCreateTime(System.currentTimeMillis());
			result = SQLiteUICDao.getInstance().addUIC(bean);
		} catch (SQLException e) {
			logger.error("add new uic to db error");
		}
		if (result) {
			logger.info("init addmin uic success");
		} else {
			logger.warn("init admin uic fail");
		}
	}

	public static Connection getConnection() {
		return sqlitConnection;
	}

}
