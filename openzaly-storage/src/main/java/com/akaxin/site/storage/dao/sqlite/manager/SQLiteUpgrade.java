package com.akaxin.site.storage.dao.sqlite.manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.akaxin.site.storage.dao.config.DBConfig;
import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.exception.UpgradeDatabaseException;
import com.akaxin.site.storage.util.FileUtils;
import com.akaxin.site.storage.util.SqlLog;

/**
 * sqlite 数据库升级
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-28 11:47:07
 */
public class SQLiteUpgrade {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUpgrade.class);

	private static final String DB_FILE_PATH = "openzalyDB.sqlite3";
	private static final String OPENZALY_SQLITE_SQL = "openzaly-sqlite.sql";

	private SQLiteUpgrade() {
	}

	public static int doUpgrade(DBConfig config) throws SQLException, UpgradeDatabaseException {
		return upgradeSqliteDB(config, false);
	}

	public static int doUpgrade(DBConfig config, boolean clear) throws SQLException, UpgradeDatabaseException {
		return upgradeSqliteDB(config, clear);
	}

	// upgrade db,return current db user-version
	private static int upgradeSqliteDB(DBConfig config, boolean clear) throws SQLException, UpgradeDatabaseException {
		// 数据库文件
		File file = new File(config.getDbDir(), DB_FILE_PATH);

		if (!file.exists()) {
			SqlLog.info("openzaly start with first init sqlite database");
			SQLiteJDBCManager.loadDatabaseDriver(config.getDbDir());
			doInitWork(SQLiteJDBCManager.getConnection());
			SQLiteJDBCManager.setDbVersion(SQLConst.SITE_DB_VERSION_11);
		} else {
			SQLiteJDBCManager.loadDatabaseDriver(config.getDbDir());
			if (clear) {
				SQLiteUnique.clearUnique(SQLiteJDBCManager.getConnection());
			}
			doUpgradeWork(config);
		}

		return SQLiteJDBCManager.getDbVersion();
	}

	/**
	 * 通过sql脚本初始化数据库表
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	private static void doInitWork(Connection conn) throws SQLException {
		try {
			// 生成临时sql文件加载数据库sql执行脚本,
			File sqlFile = new File(OPENZALY_SQLITE_SQL);
			if (!sqlFile.exists()) {
				FileUtils.writeResourceToFile("/" + OPENZALY_SQLITE_SQL, sqlFile);
			}

			// 初始化数据库表
			File file = new File(OPENZALY_SQLITE_SQL);
			if (!file.exists()) {
				throw new FileNotFoundException("init mysql with sql script file is not exists");
			}

			FileSystemResource rc = new FileSystemResource(file);
			EncodedResource encodeRes = new EncodedResource(rc, "GBK");
			ScriptUtils.executeSqlScript(conn, encodeRes);
			SqlLog.info("openzaly init sqlite with sql-script finish");

			file.delete();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private static void doUpgradeWork(DBConfig config) throws UpgradeDatabaseException, SQLException {
		int times = 5;
		while (true) {
			// 做一个简单的频率控制，防止升级出现死循环
			if (times-- <= 0) {
				break;
			}

			int dbVersion = SQLiteJDBCManager.getDbVersion();
			if (dbVersion < 9) {
				// 1.首先备份
				String fileName = backupDatabaseFile(config.getDbDir(), dbVersion);
				if (StringUtils.isEmpty(fileName)) {
					throw new UpgradeDatabaseException("backup database file before upgrade error");
				}
				// 2.升级
				if (upgrade0_9(dbVersion)) {
					SQLiteJDBCManager.setDbVersion(SQLConst.SITE_DB_VERSION_9);
				} else {
					logger.error("upgrade user-version {} -> {} error.", dbVersion, SQLConst.SITE_DB_VERSION_9);
					// db rename to original db file
					restoreDatabase(fileName);
				}
				continue;
			} else if (dbVersion == 9) {
				// 0.9.5 upgrade from 9 to 10
				// 1.首先备份
				String fileName = backupDatabaseFile(config.getDbDir(), dbVersion);
				if (StringUtils.isEmpty(fileName)) {
					throw new UpgradeDatabaseException("backup database file before upgrade error");
				}
				if (StringUtils.isEmpty(fileName)) {
					throw new UpgradeDatabaseException("backup database file before upgrade error");
				}
				// 2.升级
				if (upgrade9_10(dbVersion)) {
					SQLiteJDBCManager.setDbVersion(SQLConst.SITE_DB_VERSION_10);
				} else {
					logger.error("upgrade user-version {} -> {} error.", dbVersion, SQLConst.SITE_DB_VERSION_10);
					// db rename to original db file
					restoreDatabase(fileName);
				}

			} else if (dbVersion == 10) {
				doInitWork(SQLiteJDBCManager.getConnection());
				SQLiteJDBCManager.setDbVersion(SQLConst.SITE_DB_VERSION_11);
			}

			break;
		}
	}

	private static String backupDatabaseFile(String dbDir, int oldVersion) {
		String filePath = "./";
		try {
			String dbFileName = SQLiteJDBCManager.getDbFileName();
			if (StringUtils.isNotEmpty(dbDir)) {
				filePath = dbDir;
			}
			File file = new File(filePath, dbFileName);// original db file
			String bkFileName = dbFileName + ".backup." + oldVersion + "." + System.currentTimeMillis();
			File backupFile = new File(filePath, bkFileName);
			if (!backupFile.exists()) {
				new FileOutputStream(backupFile).close();
			}
			FileOutputStream fos = new FileOutputStream(backupFile);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			try {
				byte[] buffer = new byte[1024];
				int bytesLen = 0;
				while ((bytesLen = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesLen);
				}
			} finally {
				if (bis != null) {
					bis.close();
				}
				if (fos != null) {
					fos.close();
				}
			}

			return backupFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			logger.error("backup db file error,fileUrl=" + filePath, e);
		} catch (IOException e) {
			logger.error("backup db file IOException.fileUrl=" + filePath, e);
		}

		return null;
	}

	private static boolean upgrade0_9(int oldVersion) {
		String sql1 = "alter table " + SQLConst.SITE_USER_PROFILE + " RENAME TO "
				+ getTempTable(oldVersion, SQLConst.SITE_USER_PROFILE);
		String sql2 = "alter table " + SQLConst.SITE_USER_FRIEND + " RENAME TO "
				+ getTempTable(oldVersion, SQLConst.SITE_USER_FRIEND);
		String sql3 = "alter table " + SQLConst.SITE_USER_DEVICE + " RENAME TO "
				+ getTempTable(oldVersion, SQLConst.SITE_USER_DEVICE);

		boolean result = false;
		List<String> upgradeSqls = Arrays.asList(sql1, sql2, sql3);
		try {
			Connection conn = SQLiteJDBCManager.getConnection();
			conn.setAutoCommit(false);
			try {
				// 1.rename
				for (String sql : upgradeSqls) {
					PreparedStatement pst = conn.prepareStatement(sql);
					int res = pst.executeUpdate();
					logger.info("rename database table result={} sql:{}", res, sql);
					if (pst != null) {
						pst.close();
					}
				}
				// 2.check all tables
				doInitWork(conn);

				// 3. migrate
				String migSql1 = "INSERT INTO " + SQLConst.SITE_USER_PROFILE
						+ "(id,site_user_id,global_user_id,user_id_pubk,user_name,user_photo,phone_id,user_status,mute,register_time) select id,site_user_id,global_user_id,user_id_pubk,user_name,user_photo,phone_id,user_status,mute,register_time from "
						+ getTempTable(oldVersion, SQLConst.SITE_USER_PROFILE);

				String migSql2 = "INSERT INTO " + SQLConst.SITE_USER_FRIEND
						+ "(id,site_user_id,site_friend_id,relation,mute,add_time) select id,site_user_id,site_friend_id,relation,mute,add_time from "
						+ getTempTable(oldVersion, SQLConst.SITE_USER_FRIEND);

				String migSql3 = "INSERT INTO " + SQLConst.SITE_USER_DEVICE
						+ "(id,site_user_id,device_id,user_device_pubk,user_token,device_name,active_time,add_time) SELECT id,site_user_id,device_id,user_device_pubk,user_token,device_name,active_time,add_time FROM "
						+ getTempTable(oldVersion, SQLConst.SITE_USER_DEVICE);
				Map<Integer, String> sqlMap = new HashMap<Integer, String>() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1438260552099272302L;

					{
						put(1, migSql1);
						put(2, migSql2);
						put(3, migSql3);
					}
				};
				for (int sqlIndex : sqlMap.keySet()) {
					String sql_mig = sqlMap.get(sqlIndex);
					PreparedStatement pst = conn.prepareStatement(sql_mig);
					int res = pst.executeUpdate();
					logger.info("migrate database table result={} sql:{}", res, sql_mig);
					if (pst != null) {
						pst.close();
					}
				}
				result = true;// 兼容失败情况，这里不能使用成功个数
			} catch (Exception e) {
				logger.error("upgrade to execute sql error", e);
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("rename database table to upgrade error.", e);
		}

		return result;
	}

	private static boolean upgrade9_10(int oldVersion) {
		String sql = "ALTER TABLE " + SQLConst.SITE_USER_MESSAGE + " RENAME TO "
				+ getTempTable(oldVersion, SQLConst.SITE_USER_MESSAGE);

		boolean result = false;
		try {
			Connection conn = SQLiteJDBCManager.getConnection();
			// 1.rename
			PreparedStatement pst = conn.prepareStatement(sql);
			int res = pst.executeUpdate();
			logger.info("rename database table result={} sql:{}", res, sql);

			if (pst != null) {
				pst.close();
			}

			// 2.check all tables
			doInitWork(conn);

			// 3. migrate
			String sql_mig = "INSERT INTO " + SQLConst.SITE_USER_MESSAGE
					+ "(id,site_user_id,msg_id,send_user_id,receive_user_id,msg_type,content,device_id,ts_key,msg_time) "
					+ "SELECT id,site_user_id,msg_id,send_user_id,site_user_id,msg_type,content,device_id,ts_key,msg_time FROM "
					+ getTempTable(oldVersion, SQLConst.SITE_USER_MESSAGE);

			PreparedStatement pst2 = conn.prepareStatement(sql_mig);
			int res2 = pst2.executeUpdate();
			logger.info("migrate database table result={} sql:{}", res2, sql_mig);
			if (pst2 != null) {
				pst2.close();
			}
			result = true;// 兼容失败情况，这里不能使用成功个数
		} catch (SQLException e) {
			logger.error("execute database table to upgrade error.", e);
		}

		return result;
	}

	private static String getTempTable(int oldVersion, String tableName) {
		return "temp_" + oldVersion + "_" + tableName;
	}

	private static void restoreDatabase(String filePath) throws UpgradeDatabaseException {
		File backupFile = new File(filePath);
		if (!backupFile.exists()) {
			throw new UpgradeDatabaseException("backup file cannot find");
		}
		String dbPath = backupFile.getParent();
		File dbFile = new File(dbPath, SQLiteJDBCManager.getDbFileName());
		if (dbFile.exists()) {
			dbFile.delete();
		}
		backupFile.renameTo(dbFile);
	}

}
