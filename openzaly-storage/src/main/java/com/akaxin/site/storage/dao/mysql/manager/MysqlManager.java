package com.akaxin.site.storage.dao.mysql.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.akaxin.site.storage.exception.InitDatabaseException;
import com.akaxin.site.storage.exception.NeedInitMysqlException;
import com.akaxin.site.storage.util.SqlLog;

/**
 * Mysql 连接器管理
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-11 19:05:56
 */
public class MysqlManager {
	private static final String OPENZALY_MYSQL_SQL = "openzaly-mysql.sql";

	// 初始化数据库异常，会导致程序终端启动
	public static void initMysqlDB(Properties pro) throws InitDatabaseException, NeedInitMysqlException {
		try {
			// init db && table
			Connection conn = InitDatabaseConnection.getInitConnection(pro);
			// 初始化数据库表
			File file = new File(OPENZALY_MYSQL_SQL);
			if (!file.exists()) {
				throw new NeedInitMysqlException("init mysql with sql script file error");
			}

			FileSystemResource rc = new FileSystemResource(file);
			EncodedResource encodeRes = new EncodedResource(rc, "GBK");
			ScriptUtils.executeSqlScript(conn, encodeRes);
			SqlLog.info("openzaly init mysql database with sql-script finish");

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
