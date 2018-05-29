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

	// upgrade db
	public static void upgradeSqliteDB(DBConfig config) throws SQLException {
		SQLiteJDBCManager.loadDatabaseDriver(config.getDbDir());

		int dbVersion = SQLiteJDBCManager.getDbVersion();
		switch (dbVersion) {
		case 0:
			upgrade0_9();
		case 9:
			break;
		}

	}

	private static void upgrade0_9() {
		String sql1 = "alter table " + SQLConst.SITE_USER_PROFILE + " add site_login_id VARCHAR(50) UNIQUE;";
		String sql2 = "alter table " + SQLConst.SITE_USER_PROFILE + " add login_id_lowercase VARCHAR(50) UNIQUE;";
		String sql3 = "alter table " + SQLConst.SITE_USER_PROFILE + " add user_name_in_latin VARCHAR(50);";

		String sql4 = "alter table " + SQLConst.CREATE_SITE_USER_FRIEND_TABLE + "add alias_name VARCHAR(50);";
		String sql5 = "alter table " + SQLConst.CREATE_SITE_USER_FRIEND_TABLE + "add alias_name_in_latin VARCHAR(50);";

		List<String> upgradeSqls = Arrays.asList(sql1, sql2, sql3, sql4, sql5);
		try {
			Connection conn = SQLiteJDBCManager.getConnection();
			conn.setAutoCommit(false);
			try {
				for (String sql : upgradeSqls) {
					PreparedStatement pst = conn.prepareStatement(sql);
					boolean result = pst.execute(sql);
					logger.info("upgrade database result={} sql:{}", result, sql);
				}
			} catch (Exception e) {
				logger.error("upgrade to execute sql error", e);
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("upgrade user-version from 0 to 9 error.", e);
		}

	}
}
