package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.SQLException;
import java.util.Properties;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * C3P0 主库配置管理
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-11 18:21:38
 */
public class C3P0PoolManager extends AbstractPoolManager {
	private static ComboPooledDataSource cpds;

	public static void initPool(Properties pro) throws Exception {
		String jdbcUrl = getDBUrl(pro);
		String userName = trimToNull(pro, JdbcConst.MYSQL_USER_NAME);
		String password = trimToNull(pro, JdbcConst.MYSQL_PASSWORD);

		cpds = new ComboPooledDataSource();
		cpds.setDriverClass(MYSQL_JDBC_DRIVER); // loads the jdbc driver
		cpds.setJdbcUrl(jdbcUrl);
		cpds.setUser(userName);
		cpds.setPassword(password);
		int inititalSize = Integer.valueOf(trimToNull(pro, JdbcConst.MYSQL_INITIAL_SIZE, "10"));
		int maxSize = Integer.valueOf(trimToNull(pro, JdbcConst.MYSQL_MAX_SIZE, "100"));
		cpds.setInitialPoolSize(inititalSize);// 初始创建默认10个连接
		cpds.setMaxPoolSize(maxSize);// 最大默认100个
		int inc = (maxSize - inititalSize) / 5;
		cpds.setAcquireIncrement(Integer.max(1, inc));// 每次创建10个

		int maxIdle = Integer.valueOf(trimToNull(pro, JdbcConst.MYSQL_MAX_IDLE, "60"));
		cpds.setMaxIdleTime(maxIdle);// 最大空闲时间

	}

	public static java.sql.Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

}
