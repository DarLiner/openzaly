package com.akaxin.site.storage.mysql;

import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0PoolManager {
	private static ComboPooledDataSource cpds;

	public static void initPool() throws Exception {
		cpds = new ComboPooledDataSource();
//		cpds.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc driver
		cpds.setDriverClass("com.mysql.cj.jdbc.Driver"); // loads the jdbc driver
		// cpds.setJdbcUrl("jdbc:mysql://localhost:3306/hello?useUnicode=true&characterEncoding=utf-8&useSSL=true");
//		cpds.setJdbcUrl(
//				"jdbc:mysql://localhost:3306/hello?useUnicode=true&characterEncoding=utf-8&verifyServerCertificate=false&useSSL=false");
		cpds.setJdbcUrl(
				"jdbc:mysql://localhost:3306/openzaly?useUnicode=true&characterEncoding=utf-8&verifyServerCertificate=false&useSSL=true");
		cpds.setUser("root");
		cpds.setPassword("1234567890");
		// the settings below are optional -- c3p0 can work with defaults
		cpds.setInitialPoolSize(2);// 初始创建10个连接
		cpds.setAcquireIncrement(1);// 每次创建10个
		cpds.setMaxPoolSize(5);// 最大100个
		// cpds.setMaxIdleTime(6000);//最大空闲时间
	}

	public static java.sql.Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	public static void returnConnection() {
		// cpds.r
	}
}
