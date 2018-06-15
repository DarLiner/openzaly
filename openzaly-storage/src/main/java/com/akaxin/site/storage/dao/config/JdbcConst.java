package com.akaxin.site.storage.dao.config;

/**
 * 数据库配置中的KEY
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-09 12:51:54
 */
public interface JdbcConst {

	String OPENZALY_EDITION = "openzaly.edition";

	String SQLITE_URL = "openzaly.sqlite.url";

	// ----------------------MASTER--------------------
	String MYSQL_HOST = "openzaly.mysql.host";
	String MYSQL_PORT = "openzaly.mysql.port";
	String MYSQL_DB = "openzaly.mysql.database";

	String MYSQL_USER_NAME = "openzaly.mysql.username";
	String MYSQL_PASSWORD = "openzaly.mysql.password";
	String MYSQL_INITIAL_SIZE = "openzaly.mysql.initial-size";
	String MYSQL_MAX_SIZE = "openzaly.mysql.max-size";
	String MYSQL_MAX_IDLE = "openzaly.mysql.max-idle";

	String MYSQL_USE_UNICODE = "openzaly.mysql.useUnicode";
	String MYSQL_CHARACTER_ENCODING = "openzaly.mysql.characterEncoding";
	String MYSQL_VERIFY_SERVER_CERTIFICATE = "openzaly.mysql.verifyServerCertificate";
	String MYSQL_USE_SSL = "openzaly.mysql.useSSL";

	// ----------------------SLAVE--------------------
	String MYSQL_SLAVE_HOST = "openzaly.mysql.slave.host";
	String MYSQL_SLAVE_PORT = "openzaly.mysql.slave.port";
	String MYSQL_SLAVE_DB = "openzaly.mysql.slave.database";

	String MYSQL_SLAVE_USER_NAME = "openzaly.mysql.slave.username";
	String MYSQL_SLAVE_PASSWORD = "openzaly.mysql.slave.password";
	String MYSQL_SLAVE_INITIAL_SIZE = "openzaly.mysql.slave.initial-size";
	String MYSQL_SLAVE_MAX_SIZE = "openzaly.mysql.slave.max-size";
	String MYSQL_SLAVE_MAX_IDLE = "openzaly.mysql.slave.max-idle";

	String MYSQL_SLAVE_USE_UNICODE = "openzaly.mysql.slave.useUnicode";
	String MYSQL_SLAVE_CHARACTER_ENCODING = "openzaly.mysql.slave.characterEncoding";
	String MYSQL_SLAVE_VERIFY_SERVER_CERTIFICATE = "openzaly.mysql.slave.verifyServerCertificate";
	String MYSQL_SLAVE_USE_SSL = "openzaly.mysql.slave.useSSL";
}
