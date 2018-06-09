package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.SQLException;
import java.util.Properties;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.util.SqlLog;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0PoolManager {
	private static ComboPooledDataSource cpds;

	public static void initPool(Properties pro) throws Exception {
		String jdbcDriver = pro.getProperty(JdbcConst.DRIVER_CLASSNAME, "com.mysql.cj.jdbc.Driver");
		String jdbcUrl = InitDatabaseConnection.getDBUrl(pro);
		String userName = pro.getProperty(JdbcConst.USER_NAME);
		String password = pro.getProperty(JdbcConst.PASSWORD);
		cpds = new ComboPooledDataSource();
		cpds.setDriverClass(jdbcDriver); // loads the jdbc driver
		cpds.setJdbcUrl(jdbcUrl);
		cpds.setUser(userName);
		cpds.setPassword(password);
		int inititalSize = Integer.valueOf(pro.getProperty(JdbcConst.INITIAL_SIZE, "10").trim());
		int maxSize = Integer.valueOf(pro.getProperty(JdbcConst.MAX_SIZE, "100").trim());
		cpds.setInitialPoolSize(inititalSize);// 初始创建默认10个连接
		cpds.setMaxPoolSize(maxSize);// 最大默认100个
		int inc = (maxSize - inititalSize) / 5;
		cpds.setAcquireIncrement(Integer.max(1, inc));// 每次创建10个

		int maxIdle = Integer.valueOf(pro.getProperty(JdbcConst.MAX_IDLE, "60").trim());
		cpds.setMaxIdleTime(maxIdle);// 最大空闲时间

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
				SqlLog.error("return connection error", e);
			}
		}
	}
}
