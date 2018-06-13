package com.akaxin.site.storage.dao.mysql.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.util.SqlLog;

/**
 * C3P0 连接池塘，公共类
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-11 18:16:33
 */
public abstract class AbstractPoolManager {

	protected static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

	// 先从系统properties加载，在从指定pro中加载
	private static String trimSystemProToNull(Properties pro, String key) {
		String sysStr = StringUtils.trimToNull(System.getProperty(key));
		return sysStr != null ? sysStr : StringUtils.trimToNull(pro.getProperty(key));
	}

	protected static String trimToNull(Properties pro, String key) {
		return StringUtils.trimToNull(pro.getProperty(key));
	}

	protected static String trimToNull(Properties pro, String key, String def) {
		String trimedStr = StringUtils.trimToNull(pro.getProperty(key));
		return trimedStr != null ? trimedStr : def;
	}

	// 释放连接池资源
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

	public static String getDBUrl(Properties pro) throws Exception {
		String host = trimToNull(pro, JdbcConst.MYSQL_HOST);
		String port = trimToNull(pro, JdbcConst.MYSQL_PORT);
		String dbName = trimToNull(pro, JdbcConst.MYSQL_DB);

		// "jdbc:mysql://localhost:3306/mysql?";
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

		if (StringUtils.isAnyEmpty(host, port, dbName)) {
			throw new Exception("jdbc url=" + url + " is invalid");
		}

		String useUnicode = trimToNull(pro, JdbcConst.MYSQL_USE_UNICODE, "true");
		String characterEncoding = trimToNull(pro, JdbcConst.MYSQL_CHARACTER_ENCODING, "utf-8");
		String verifyServerCertificate = trimToNull(pro, JdbcConst.MYSQL_VERIFY_SERVER_CERTIFICATE, "false");
		String useSSL = trimToNull(pro, JdbcConst.MYSQL_USE_SSL, "true");

		StringBuilder sb = new StringBuilder(url);
		sb.append("?useUnicode=");
		sb.append(useUnicode);

//		sb.append("&characterEncoding=");
//		sb.append(characterEncoding);

		sb.append("&verifyServerCertificate=");
		sb.append(verifyServerCertificate);

		sb.append("&useSSL=");
		sb.append(useSSL);

		return sb.toString();
	}

	public static List<String> getSlaveDBUrl(Properties pro) throws Exception {
		String slaveHosts = trimToNull(pro, JdbcConst.MYSQL_SLAVE_HOST);// 192.168.3.4,192.168.3.5,192.168.3.6

		if (StringUtils.isEmpty(slaveHosts)) {
			return null;
		}

		List<String> urlList = new ArrayList<String>();
		String[] hosts = slaveHosts.split(",");

		for (String host : hosts) {
			host = StringUtils.trimToNull(host);
			String port = trimToNull(pro, JdbcConst.MYSQL_PORT);
			String dbName = trimToNull(pro, JdbcConst.MYSQL_DB);

			// "jdbc:mysql://localhost:3306/mysql?";
			String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

			if (StringUtils.isAnyEmpty(host, port, dbName)) {
				throw new Exception("mysql slave jdbc url=" + url + " is invalid");
			}

			String useUnicode = pro.getProperty(JdbcConst.MYSQL_SLAVE_USE_UNICODE, "true");
			String characterEncoding = pro.getProperty(JdbcConst.MYSQL_SLAVE_CHARACTER_ENCODING, "utf-8");
			String verifyServerCertificate = pro.getProperty(JdbcConst.MYSQL_SLAVE_VERIFY_SERVER_CERTIFICATE, "false");
			String useSSL = pro.getProperty(JdbcConst.MYSQL_SLAVE_USE_SSL, "true");

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

			urlList.add(sb.toString());
		}
		return urlList;
	}
}
