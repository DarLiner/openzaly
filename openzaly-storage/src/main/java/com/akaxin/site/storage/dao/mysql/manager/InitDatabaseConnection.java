package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 启动mysql的情形下，需要获取一个连接建数据库，建表操作，只有在初始化数据库情况下才使用
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 16:18:11
 */
public class InitDatabaseConnection {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8&verifyServerCertificate=false&useSSL=true";

	static final String USER = "root";
	static final String PASS = "1234567890";

	private static Connection conn;

	public static Connection getInitConnection() throws Exception {
		Class.forName(JDBC_DRIVER);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}

	public static void closeInitConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
