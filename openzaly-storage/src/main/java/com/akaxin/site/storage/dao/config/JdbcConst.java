package com.akaxin.site.storage.dao.config;

/**
 * 数据库配置中的KEY
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-09 12:51:54
 */
public interface JdbcConst {
	String DRIVER_CLASSNAME = "openzaly.datasource.driverClassName";
	String DB_URL = "openzaly.datasource.url";

	String USER_NAME = "openzaly.datasource.username";
	String PASSWORD = "openzaly.datasource.password";
	String INITIAL_SIZE = "openzaly.datasource.initial-size";
	String MAX_SIZE = "openzaly.datasource.max-size";
	String MAX_IDLE = "openzaly.datasource.max-idle";

	String USE_UNICODE = "openzaly.datasource.useUnicode";
	String CHARACTER_ENCODING = "openzaly.datasource.characterEncoding";
	String VERIFY_SERVER_CERTIFICATE = "openzaly.datasource.verifyServerCertificate";
	String USE_SSL = "openzaly.datasource.useSSL";
}
