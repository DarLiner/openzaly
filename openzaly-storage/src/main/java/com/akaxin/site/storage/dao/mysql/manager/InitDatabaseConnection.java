package com.akaxin.site.storage.dao.mysql.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.util.FileUtils;
import com.akaxin.site.storage.util.SqlLog;

/**
 * 启动mysql的情形下，需要获取一个连接建数据库，建表操作，只有在初始化数据库情况下才使用
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 16:18:11
 */
public class InitDatabaseConnection extends AbstractPoolManager {

	private static final String OPENZALY_MYSQL_SQL = "openzaly-mysql.sql";
	private static Connection conn;

	public static void init(Properties prop) throws Exception {
		try {
			initAndGetConnection(prop);
		} finally {
			closeInitConnection();
		}
	}

	public static Connection initAndGetConnection(Properties prop) throws Exception {
		Connection conn = initConnection(prop);
		String dbName = trimToNull(prop, JdbcConst.MYSQL_DB);
		createAndUseDatabase(conn, dbName);
		initDatabaseTable(conn);
		return conn;
	}

	private static Connection initConnection(Properties pro) throws Exception {
		// "com.mysql.cj.jdbc.Driver"
		String jdbcUrl = getDBUrlWithoutDBName(pro);
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

	private static void createAndUseDatabase(Connection conn, String dbName) throws SQLException {
		if (StringUtils.isEmpty(dbName)) {
			throw new SQLException("openzaly team need designated database name,but now it's " + dbName);
		}
		String createSql = "CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4;";
		String useSql = "use " + dbName;
		conn.prepareStatement(createSql).executeUpdate();
		conn.prepareStatement(useSql).executeUpdate();
	}

	private static void initDatabaseTable(Connection conn) throws SQLException {
		try {
			// 生成临时sql文件加载数据库sql执行脚本,
			File sqlFile = new File(OPENZALY_MYSQL_SQL);
			if (!sqlFile.exists()) {
				FileUtils.writeResourceToFile("/" + OPENZALY_MYSQL_SQL, sqlFile);
			}

			// 初始化数据库表
			File file = new File(OPENZALY_MYSQL_SQL);
			if (!file.exists()) {
				throw new FileNotFoundException("init mysql with sql script file is not exists");
			}

			FileSystemResource rc = new FileSystemResource(file);
			EncodedResource encodeRes = new EncodedResource(rc, "GBK");
			ScriptUtils.executeSqlScript(conn, encodeRes);
			SqlLog.info("openzaly init mysql database with sql-script finish");

			file.delete();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
