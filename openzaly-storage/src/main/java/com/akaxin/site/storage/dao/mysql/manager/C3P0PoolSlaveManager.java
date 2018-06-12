package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.util.SqlLog;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-11 17:56:55
 */
public class C3P0PoolSlaveManager extends AbstractPoolManager {

	private static List<ComboPooledDataSource> cpdsList;

	public static void initPool(Properties pro) throws Exception {
		List<String> jdbcUrlList = getSlaveDBUrl(pro);

		String userName = trimToNull(pro, JdbcConst.MYSQL_SLAVE_USER_NAME);
		String password = trimToNull(pro, JdbcConst.MYSQL_SLAVE_PASSWORD);

		if (jdbcUrlList == null || jdbcUrlList.size() == 0 || StringUtils.isAnyEmpty(userName, password)) {
			SqlLog.warn(
					"load database slave for mysql fail, system will user mysql master connection pool.urls={} user={} passwd={}",
					jdbcUrlList, userName, password);
			return;
		}

		cpdsList = new ArrayList<ComboPooledDataSource>();

		for (String jdbcUrl : jdbcUrlList) {

			ComboPooledDataSource cpds = new ComboPooledDataSource();
			cpds.setDriverClass(MYSQL_JDBC_DRIVER); // loads the jdbc driver
			cpds.setJdbcUrl(jdbcUrl);
			cpds.setUser(userName);
			cpds.setPassword(password);
			int inititalSize = Integer.valueOf(trimToNull(pro, JdbcConst.MYSQL_SLAVE_INITIAL_SIZE, "10"));
			int maxSize = Integer.valueOf(trimToNull(pro, JdbcConst.MYSQL_SLAVE_MAX_SIZE, "100"));
			cpds.setInitialPoolSize(inititalSize);// 初始创建默认10个连接
			cpds.setMaxPoolSize(maxSize);// 最大默认100个
			int inc = (maxSize - inititalSize) / 5;
			cpds.setAcquireIncrement(Integer.max(1, inc));// 每次创建个数

			int maxIdle = Integer.valueOf(trimToNull(pro, JdbcConst.MYSQL_SLAVE_MAX_IDLE, "60"));
			cpds.setMaxIdleTime(maxIdle);// 最大空闲时间

			cpdsList.add(cpds);
		}
	}

	// 如果没有配置从库，直接使用主库连接池
	public static java.sql.Connection getConnection() throws SQLException {
		if (cpdsList == null || cpdsList.size() == 0) {
			return C3P0PoolManager.getConnection();
		}
		int index = RandomUtils.nextInt(0, cpdsList.size());// [0,len)
		return cpdsList.get(index).getConnection();
	}

}
