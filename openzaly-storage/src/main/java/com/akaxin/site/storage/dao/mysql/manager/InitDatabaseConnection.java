package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.util.SqlLog;

/**
 * 启动mysql的情形下，需要获取一个连接建数据库，建表操作，只有在初始化数据库情况下才使用
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 16:18:11
 */
public class InitDatabaseConnection {
	private static Connection conn;

	public static Connection getInitConnection(Properties pro) throws Exception {
		String jdbcDriver = pro.getProperty(JdbcConst.DRIVER_CLASSNAME, "com.mysql.cj.jdbc.Driver");
		String jdbcUrl = getDBUrl(pro);
		String userName = pro.getProperty(JdbcConst.USER_NAME);
		String password = pro.getProperty(JdbcConst.PASSWORD);
		Class.forName(jdbcDriver);
		System.out.println(String.format("%s %s %s", jdbcUrl, userName, password));
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

	public static String getDBUrl(Properties pro) throws Exception {
		String url = pro.getProperty(JdbcConst.DB_URL);

		if (StringUtils.isEmpty(url)) {
			throw new Exception("jdbc url is empty");
		}

		String useUnicode = pro.getProperty(JdbcConst.USE_UNICODE, "true");
		String characterEncoding = pro.getProperty(JdbcConst.CHARACTER_ENCODING, "utf-8");
		String verifyServerCertificate = pro.getProperty(JdbcConst.VERIFY_SERVER_CERTIFICATE, "false");
		String useSSL = pro.getProperty(JdbcConst.USE_SSL, "true");

		StringBuilder sb = new StringBuilder(url);
		if (url.contains("?")) {
			sb.append("&useUnicode=");
		} else {
			sb.append("?useUnicode=");
		}
		sb.append(useUnicode);
		sb.append("&characterEncoding=");
		sb.append(characterEncoding);
		sb.append("&verifyServerCertificate=");
		sb.append(verifyServerCertificate);
		sb.append("&useSSL=");
		sb.append(useSSL);

		return sb.toString();
	}

}
