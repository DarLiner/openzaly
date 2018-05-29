package com.akaxin.site.storage.sqlite.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.sqlite.sql.SQLConst;

public class SQLiteUpgrade {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUpgrade.class);

	private SQLiteUpgrade() {
	}

	// upgrade db,return current db user-version
	public static int upgradeSqliteDB(DBConfig config) throws SQLException {
		SQLiteJDBCManager.loadDatabaseDriver(config.getDbDir());

		int dbVersion = SQLiteJDBCManager.getDbVersion();
		switch (dbVersion) {
		case 0:
			if (upgrade0_9()) {
				SQLiteJDBCManager.setDbVersion(SQLConst.SITE_DB_VERSION);
			} else {
				logger.error("upgrade user-version {} -> {} error.", dbVersion, SQLConst.SITE_DB_VERSION);
			}
			break;
		case 9:
			break;
		}

		return SQLiteJDBCManager.getDbVersion();
	}

	private static boolean upgrade0_9() {
		String sql1 = "alter table " + SQLConst.SITE_USER_PROFILE + " add site_login_id VARCHAR(50) UNIQUE;";
		String sql2 = "alter table " + SQLConst.SITE_USER_PROFILE + " add login_id_lowercase VARCHAR(50) UNIQUE;";
		String sql3 = "alter table " + SQLConst.SITE_USER_PROFILE + " add user_name_in_latin VARCHAR(50);";

		String sql4 = "alter table " + SQLConst.CREATE_SITE_USER_FRIEND_TABLE + "add alias_name VARCHAR(50);";
		String sql5 = "alter table " + SQLConst.CREATE_SITE_USER_FRIEND_TABLE + "add alias_name_in_latin VARCHAR(50);";

		boolean result = false;
		List<String> upgradeSqls = Arrays.asList(sql1, sql2, sql3, sql4, sql5);
		try {
			Connection conn = SQLiteJDBCManager.getConnection();
			conn.setAutoCommit(false);
			try {
				for (String sql : upgradeSqls) {
					PreparedStatement pst = conn.prepareStatement(sql);
					boolean res = pst.execute(sql);
					logger.info("upgrade database result={} sql:{}", res, sql);
				}
				result = true;// 兼容失败情况，这里不能使用成功个数
			} catch (Exception e) {
				logger.error("upgrade to execute sql error", e);
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("upgrade user-version from 0 to 9 error.", e);
		}
		return result;
	}
}
