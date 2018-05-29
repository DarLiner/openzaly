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
import com.akaxin.site.storage.exception.UpgradeDatabaseException;
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

	private static int SITE_DB_VERSION = SQLConst.SITE_DB_VERSION;
	private static String sqliteDriverName = "org.sqlite.JDBC";
	private static Connection sqlitConnection = null;
	private static final String DB_FILE_PATH = "openzalyDB.sqlite3";

	private SQLiteJDBCManager() {

	}

	// init db
	public static void initSqliteDB(DBConfig config) throws SQLException, UpgradeDatabaseException {
		loadDatabaseDriver(config.getDbDir());

		checkDatabaseBeforeRun();

		initSiteConfig(config.getConfigMap());
		addSitePlugin(1, PluginArgs.SITE_ADMIN_NAME, config.getAdminApi(), config.getSiteServer(),
				config.getAdminIcon());
		addSitePlugin(2, PluginArgs.FRIEND_SQUARE_NAME, PluginArgs.FRIEND_SQUARE_API, config.getSiteServer(),
				config.getParam(PluginArgs.FRIEND_SQUARE, String.class));
		initAdminUic(config.getAdminUic());
	}

	public static void loadDatabaseDriver(String dbDir) {
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
			// logger.info("load data base connectionUrl={}", dbUrl);
			sqlitConnection = DriverManager.getConnection(dbUrl);
		} catch (ClassNotFoundException e) {
			logger.error("class not found.", e);
		} catch (SQLException e) {
			logger.error("load sqlite driver error.", e);
		}
	}

	private static void checkDatabaseBeforeRun() throws SQLException, UpgradeDatabaseException {
		int dbVersion = getDbVersion();
		logger.info("SQLite current user-version:{}", dbVersion);
		if (dbVersion < SITE_DB_VERSION) {
			int num = checkDatabaseTable();
			if (num == SQLConst.SITE_TABLES_MAP.size()) {
				// database index
				checkDatabaseIndex();
				// 版本设置为 SITE_DB_VERSION
				setDbVersion(SITE_DB_VERSION);
				logger.info("create all database tables finish, currentuser-version:{}", getDbVersion());
			} else {
				// 提醒用户升级
				throw new UpgradeDatabaseException(
						"Openzaly-server need upgrade SQLite database,from user-version: {} to {}", dbVersion,
						SITE_DB_VERSION);
			}
		} else {
			logger.info("SQLite is latest version:{} with Openzaly-server", SITE_DB_VERSION);
		}

		// checkDatabaseVersion();
	}

	public static int getDbVersion() throws SQLException {
		PreparedStatement pst = sqlitConnection.prepareStatement("PRAGMA user_version");
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public static void setDbVersion(int version) throws SQLException {
		String sql = "PRAGMA user_version=" + version;
		PreparedStatement pst = sqlitConnection.prepareStatement(sql);
		pst.executeUpdate();
	}

	public static int checkDatabaseTable() {
		int num = 0;
		for (String tableName : SQLConst.SITE_TABLES_MAP.keySet()) {
			int result = createTable(tableName, SQLConst.SITE_TABLES_MAP.get(tableName));
			num += result;
			logger.info("create table:{} {}", tableName, result == 1 ? "OK" : "false");
		}
		return num;
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
		String checkTableSql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=? AND tbl_name=?;";
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

	private static int createTable(String tableName, String createTableSQL) {
		try {
			if (existTable(tableName)) {
				return 0;
			}
			PreparedStatement pst = sqlitConnection.prepareStatement(createTableSQL);
			pst.executeUpdate();
			// 再次检测是否创建成功
			if (existTable(tableName)) {
				return 1;
			}
		} catch (Exception e) {
			logger.error("create table " + tableName + " sql=" + createTableSQL + " error.,", e);
		}
		return 0;
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

	private static void initSiteConfig(Map<Integer, String> configMap) {
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
				if (oldMap.get(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE);
				}
			}
			SQLiteSiteConfigDao.getInstance().updateSiteConfig(configMap, true);
		} catch (SQLException e) {
			logger.error("init site config error.");
		}
	}

	private static void addSitePlugin(int id, String siteName, String urlPage, String apiUrl, String siteIcon) {
		boolean result = false;
		String updateSql = "UPDATE site_plugin_manager SET "//
				+ "name=?,"//
				+ "url_page=?,"//
				+ "api_url=?,"//
				+ "auth_key=?"//
				+ " WHERE id=?;";//
		String insertSql = "INSERT INTO site_plugin_manager("//
				+ "id,"//
				+ "name,"//
				+ "icon,"//
				+ "url_page,"//
				+ "api_url,"//
				+ "auth_key,"//
				+ "allowed_ip,"//
				+ "position,"//
				+ "sort,"//
				+ "display_mode,"//
				+ "permission_status,"//
				+ "add_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement pst = sqlitConnection.prepareStatement(updateSql);
			pst.setString(1, siteName);
			pst.setString(2, urlPage);
			pst.setString(3, apiUrl);
			pst.setString(4, "");
			pst.setInt(5, id);

			result = (pst.executeUpdate() > 0);
			logger.info("update site plugin result={} SQL={} name={} url_page={} url_api={}", result, updateSql,
					siteName, urlPage, apiUrl);
		} catch (SQLException e) {
			logger.error("update site plugin error", e);
		}

		try {
			if (!result) {
				PreparedStatement pst = sqlitConnection.prepareStatement(insertSql);
				pst.setInt(1, id);
				pst.setString(2, siteName);
				pst.setString(3, siteIcon);
				pst.setString(4, urlPage);
				pst.setString(5, apiUrl);
				if (id == 1) {// 默认为后台管理
					pst.setString(6, "");// authkey
					pst.setString(7, "127.0.0.1");// allowed_ip
					pst.setInt(8, PluginProto.PluginPosition.HOME_PAGE_VALUE);// position
					pst.setInt(9, 0);// sort
					pst.setInt(10, PluginProto.PluginDisplayMode.NEW_PAGE_VALUE); // display_mode
					pst.setInt(11, PluginProto.PermissionStatus.DISABLED_VALUE); // permission_status
					pst.setLong(12, System.currentTimeMillis()); // add_time
				} else {
					pst.setString(6, "");// authkey
					pst.setString(7, "127.0.0.1");// allowed_ip
					pst.setInt(8, PluginProto.PluginPosition.HOME_PAGE_VALUE);// position
					pst.setInt(9, 1);// sort
					pst.setInt(10, PluginProto.PluginDisplayMode.NEW_PAGE_VALUE); // display_mode
					pst.setInt(11, PluginProto.PermissionStatus.AVAILABLE_VALUE); // permission_status
					pst.setLong(12, System.currentTimeMillis()); // add_time
				}
				result = (pst.executeUpdate() > 0);
				logger.info("insert site plugin result={} SQL={} name={} url_page={} url_api={}", result, insertSql,
						siteName, urlPage, apiUrl);
			}
		} catch (SQLException e) {
			logger.error("insert site plugin error", e);
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
			logger.warn("add new uic to db error,you can ignore it");
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

	public static String getDbFileName() {
		return DB_FILE_PATH;
	}
}
