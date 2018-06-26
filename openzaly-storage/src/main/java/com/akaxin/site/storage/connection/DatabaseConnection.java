package com.akaxin.site.storage.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.dao.config.DBType;
import com.akaxin.site.storage.dao.mysql.manager.MysqlManager;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;

/**
 * 根据项目使用的数据库类型，选择不同的Connection
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 17:48:30
 */
public class DatabaseConnection {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
	private static final DBType DATABASE_TYPE = DBType.getDBType(System.getProperty("database"));

	public static Connection getConnection(boolean isMaster) throws SQLException {
		return isMaster ? getConnection() : getSlaveConnection();
	}

	public static Connection getConnection() throws SQLException {
		switch (DATABASE_TYPE) {
		case PERSONAL:
			return SQLiteJDBCManager.getConnection();
		case TEAM:
			Connection conn =  MysqlManager.getConnection();
			conn.prepareStatement("SET NAMES utf8mb4;").executeUpdate();
			return conn;
		}
		return null;
	}

	public static Connection getSlaveConnection() throws SQLException {
		switch (DATABASE_TYPE) {
		case PERSONAL:
			return SQLiteJDBCManager.getConnection();
		case TEAM:
			Connection conn =  MysqlManager.getConnection();
			conn.prepareStatement("SET NAMES utf8mb4;").executeUpdate();
			return conn;
		}
		return null;
	}

	public static void returnConnection(Connection conn) {
		switch (DATABASE_TYPE) {
		case PERSONAL:
			break;
		case TEAM:
			MysqlManager.returnConnection(conn);
			return;
		}
	}

	public static void returnConnection(Connection conn, PreparedStatement ps) {
		closePreparedStatement(ps);
		switch (DATABASE_TYPE) {
		case PERSONAL:
			break;
		case TEAM:
			MysqlManager.returnConnection(conn);
			return;
		}
	}

	public static void returnConnection(Connection conn, PreparedStatement pst, ResultSet rs) {
		closePreparedStatement(pst);
		closeResultSet(rs);
		switch (DATABASE_TYPE) {
		case PERSONAL:
			break;
		case TEAM:
			MysqlManager.returnConnection(conn);
			return;
		}
	}

	public static void closePreparedStatement(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			logger.error("close PreparedStatement error.", e);
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			logger.error("cloase ResultSet error.", e);
		}
	}
}
