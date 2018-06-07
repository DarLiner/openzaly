package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.akaxin.site.storage.exception.InitDatabaseException;

public class MysqlManager {
	private static final Logger logger = LoggerFactory.getLogger(MysqlManager.class);

	// 初始化数据库异常，会导致程序终端启动
	public static void initMysqlDB() throws InitDatabaseException {
		try {
			// init db && table
			Connection conn = InitDatabaseConnection.getInitConnection();
			String sqlPath = "/Users/anguoyue/git/openzaly/openzaly-storage/src/main/resources/openzaly-mysql.sql";
			FileSystemResource rc = new FileSystemResource(sqlPath);
			EncodedResource encodeRes = new EncodedResource(rc, "GBK");
			ScriptUtils.executeSqlScript(conn, encodeRes);

			// init c3p0 pool
			C3P0PoolManager.initPool();
		} catch (Exception e) {
			logger.error("init mysql database error");
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

	public static void main(String[] args) {
		try {
			// init mysqldb
			initMysqlDB();

			// do action
			for (int i = 0; i < 5; i++) {

				System.out.println("times = " + i);
				Connection conn = C3P0PoolManager.getConnection();

				PreparedStatement ps = conn.prepareStatement("select * from site_config_info;");
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					System.out.println(rs.getInt(1) + " " + rs.getString(2));
				}

				C3P0PoolManager.returnConnection(conn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
