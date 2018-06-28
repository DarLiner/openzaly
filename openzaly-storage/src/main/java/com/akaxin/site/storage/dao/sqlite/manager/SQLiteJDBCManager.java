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
package com.akaxin.site.storage.dao.sqlite.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.dao.config.DBConfig;
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.dao.sql.SQLIndex;
import com.akaxin.site.storage.exception.UpgradeDatabaseException;

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

	private static int SITE_DB_VERSION = SQLConst.SITE_DB_VERSION_10;
	private static String sqliteDriverName = "org.sqlite.JDBC";
	private static Connection sqlitConnection = null;
	private static final String DB_FILE_PATH = "openzalyDB.sqlite3";

	private SQLiteJDBCManager() {

	}

	public static void initSqliteDB(DBConfig config) throws SQLException, UpgradeDatabaseException {
		// try to upgrade
		SQLiteUpgrade.upgradeSqliteDB(config);

		// reload sqlite driver
		loadDatabaseDriver(config.getDbDir());

		if (getDbVersion() < SITE_DB_VERSION) {
			throw new UpgradeDatabaseException("openzaly-server need to upgrade before run it");
		}

	}

	// 指定目录，加载默认数据库dbDir目录下存放默认数据库
	public static void loadDatabaseDriver(String dbDir) {
		try {
			if (sqlitConnection != null) {
				sqlitConnection.close();
			}

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

	// 指定目录，加载默认数据库dbDir目录下存放默认数据库
	public static void loadDriver(String dbFile) throws Exception {
		if (sqlitConnection != null) {
			sqlitConnection.close();
		}

		Class.forName(sqliteDriverName);
		String dbUrl = "jdbc:sqlite:" + dbFile;
		sqlitConnection = DriverManager.getConnection(dbUrl);
	}

	public static void checkDatabaseBeforeRun() throws SQLException, UpgradeDatabaseException {
		int dbVersion = getDbVersion();
		logger.info("SQLite current user-version : {}", dbVersion);

		// 不是最新版本，启动需要创建表
		if (dbVersion < SITE_DB_VERSION) {
			int num = checkDatabaseTable();
			if (num == SQLConst.SITE_TABLES_MAP.size()) {
				// database index
				checkDatabaseIndex();
				// 版本设置为 SITE_DB_VERSION
				setDbVersion(SITE_DB_VERSION);
				logger.info("create all database tables finish, currentuser-version:{}", getDbVersion());
			}
		}
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

	public static Connection getConnection() {
		return sqlitConnection;
	}

	public static String getDbFileName() {
		return DB_FILE_PATH;
	}
}
