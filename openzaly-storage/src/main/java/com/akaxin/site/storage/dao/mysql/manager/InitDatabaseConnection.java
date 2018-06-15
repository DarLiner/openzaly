package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.util.SqlLog;

/**
 * 启动mysql的情形下，需要获取一个连接建数据库，建表操作，只有在初始化数据库情况下才使用
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 16:18:11
 */
public class InitDatabaseConnection extends AbstractPoolManager {
	private static Connection conn;

	public static Connection getInitConnection(Properties pro) throws Exception {
		// "com.mysql.cj.jdbc.Driver"
		String jdbcUrl = getDBUrl(pro);
		jdbcUrl = jdbcUrl.replace("openzaly", "mysql");
		String userName = trimToNull(pro, JdbcConst.MYSQL_USER_NAME);
		String password = trimToNull(pro, JdbcConst.MYSQL_PASSWORD);
		Class.forName(MYSQL_JDBC_DRIVER);
		conn = DriverManager.getConnection(jdbcUrl, userName, password);
		return conn;
	}

	// 获取单独一个连接，迁移数据库使用
	public static Connection getConnection(Properties pro) throws Exception {
		// "com.mysql.cj.jdbc.Driver"
		String jdbcUrl = getDBUrl(pro);
		String userName = trimToNull(pro, JdbcConst.MYSQL_USER_NAME);
		String password = trimToNull(pro, JdbcConst.MYSQL_PASSWORD);
		Class.forName(MYSQL_JDBC_DRIVER);
		conn = DriverManager.getConnection(jdbcUrl, userName, password);
		return conn;
	}

	public static void closeInitConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			SqlLog.error("close mysql init connection error", e);
		}
	}

}
