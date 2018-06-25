package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.akaxin.site.storage.exception.InitDatabaseException;
import com.akaxin.site.storage.exception.NeedInitMysqlException;

/**
 * Mysql 连接器管理
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-11 19:05:56
 */
public class MysqlManager {

	// 初始化数据库异常，会导致程序终端启动
	public static void initMysqlDB(Properties pro) throws InitDatabaseException, NeedInitMysqlException {
		try {
			// init db && table
			InitDatabaseConnection.init(pro);

			// init c3p0 pool
			C3P0PoolManager.initPool(pro);
			C3P0PoolSlaveManager.initPool(pro);
		} catch (Exception e) {
			throw new InitDatabaseException("init mysql database error", e);
		} finally {
			InitDatabaseConnection.closeInitConnection();
		}

	}

	// 获取主库连接
	public static Connection getConnection() throws SQLException {
		return C3P0PoolManager.getConnection();
	}

	// 获取从库连接
	public static Connection getSalveConnection() throws SQLException {
		return C3P0PoolSlaveManager.getConnection();
	}

	// 释放资源
	public static void returnConnection(Connection conn) {
		C3P0PoolManager.returnConnection(conn);
	}

	public static String trimToNull(Properties pro, String key) {
		return C3P0PoolManager.trimToNull(pro, key);
	}

}
