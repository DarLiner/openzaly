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

import com.akaxin.site.storage.dao.sql.SQLConst;
import com.akaxin.site.storage.exception.UpgradeDatabaseException;

public class SQLiteUpgrade {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUpgrade.class);

	private SQLiteUpgrade() {
	}

	// upgrade db,return current db user-version
	public static int upgradeSqliteDB(DBConfig config) throws SQLException, UpgradeDatabaseException {
		SQLiteJDBCManager.loadDatabaseDriver(config.getDbDir());

		int dbVersion = SQLiteJDBCManager.getDbVersion();
		if (dbVersion < 9) {
			// 1.首先备份
			String fileName = backupDatabaseFile(config.getDbDir(), dbVersion);
			if (StringUtils.isEmpty(fileName)) {
				throw new UpgradeDatabaseException("backup database file before upgrade error");
			}
			// 2.升级
			if (upgrade0_9(dbVersion)) {
				SQLiteJDBCManager.setDbVersion(SQLConst.SITE_DB_VERSION);
			} else {
				logger.error("upgrade user-version {} -> {} error.", dbVersion, SQLConst.SITE_DB_VERSION);
				// db rename to original db file
				restoreDatabase(fileName);
			}
		} else if (dbVersion >= 9) {
			// latest db user-version do nothing
		}

		return SQLiteJDBCManager.getDbVersion();
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
				}
				// 2.chekc all tables
				SQLiteJDBCManager.checkDatabaseTable();

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
