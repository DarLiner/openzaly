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

			// init c3p0 pool
			C3P0PoolManager.initPool(pro);

		} catch (Exception e) {
			throw new InitDatabaseException("init mysql database error", e);
		} finally {
			InitDatabaseConnection.closeInitConnection();
		}

	}

	public static Connection getConnection() throws SQLException {
		return C3P0PoolManager.getConnection();
	}

	public static void returnConnection(Connection conn) {
		C3P0PoolManager.returnConnection(conn);
	}

}
