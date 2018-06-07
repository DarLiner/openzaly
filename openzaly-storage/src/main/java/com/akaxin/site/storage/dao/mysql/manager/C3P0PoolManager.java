package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0PoolManager {
	private static ComboPooledDataSource cpds;

	public static void initPool() throws Exception {
		cpds = new ComboPooledDataSource();
		// cpds.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc driver
		cpds.setDriverClass("com.mysql.cj.jdbc.Driver"); // loads the jdbc driver
		// cpds.setJdbcUrl("jdbc:mysql://localhost:3306/hello?useUnicode=true&characterEncoding=utf-8&useSSL=true");
		// cpds.setJdbcUrl(
		// "jdbc:mysql://localhost:3306/hello?useUnicode=true&characterEncoding=utf-8&verifyServerCertificate=false&useSSL=false");
		cpds.setJdbcUrl(
				"jdbc:mysql://localhost:3306/openzaly?useUnicode=true&characterEncoding=utf-8&verifyServerCertificate=false&useSSL=true");
		cpds.setUser("root");
		cpds.setPassword("1234567890");
		cpds.setInitialPoolSize(10);// 初始创建10个连接
		cpds.setAcquireIncrement(10);// 每次创建10个
		cpds.setMaxPoolSize(100);// 最大100个
		// cpds.setMaxIdleTime(6000);//最大空闲时间

	}

	public static java.sql.Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	public static void returnConnection(java.sql.Connection conn) {
		// cpds.r
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
