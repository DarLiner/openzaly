package com.akaxin.site.storage.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.akaxin.site.storage.dao.config.DBType;
import com.akaxin.site.storage.dao.mysql.manager.MysqlManager;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;

/**
 * 根据项目使用的数据库类型，选择不同的Connection
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 17:48:30
 */
public class DatabaseConnection {

	private static final DBType DATABASE_TYPE = DBType.getDBType(System.getProperty("database"));

	public static Connection getConnection() throws SQLException {
		switch (DATABASE_TYPE) {
		case SQLITE:
			return SQLiteJDBCManager.getConnection();
		case MYSQL:
			return MysqlManager.getConnection();
		}
		return null;
	}

	public static void returnConnection(Connection conn) {
		switch (DATABASE_TYPE) {
		case SQLITE:
			break;
		case MYSQL:
			MysqlManager.returnConnection(conn);
			return;
		}
	}
}
