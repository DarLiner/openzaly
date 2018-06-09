/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.site.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.dao.config.DBConfig;
import com.akaxin.site.storage.dao.config.DBType;
import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.dao.config.PrepareSiteConfigData;
import com.akaxin.site.storage.dao.mysql.manager.MysqlManager;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteUpgrade;
import com.akaxin.site.storage.exception.InitDatabaseException;
import com.akaxin.site.storage.exception.NeedInitMysqlException;
import com.akaxin.site.storage.exception.UpgradeDatabaseException;

/**
 * 数据源初始化管理，不做具体操作对外提供方法
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:15:15
 */
public class DataSourceManager {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceManager.class);

	private static final String OPENZALY_DATABASE_CONFIG = "openzaly-datasource.config";
	private static final String OPENZALY_MYSQL_SQL = "openzaly-mysql.sql";

	private DataSourceManager() {
	}

	public static void init(DBConfig config)
			throws InitDatabaseException, UpgradeDatabaseException, NeedInitMysqlException {
		try {
			DBType dbType = DBType.SQLITE;
			Properties pro = loadDatabaseConfig(OPENZALY_DATABASE_CONFIG);
			if (pro != null && pro.size() > 0) {
				String dbDriver = pro.getProperty(JdbcConst.DRIVER_CLASSNAME);
				if (StringUtils.isNotEmpty(dbDriver) && dbDriver.contains("com.mysql")) {
					dbType = DBType.MYSQL;
				}
			}
			config.setDb(dbType);
			logger.info("load database config finish databaseType:{}", dbType);

			switch (dbType) {
			case SQLITE:
				System.setProperty("database", dbType.getName());
				SQLiteJDBCManager.initSqliteDB(config);
				break;
			case MYSQL:
				System.setProperty("database", dbType.getName());
				MysqlManager.initMysqlDB(pro); // 初始化数据库以及数据库连接
				PrepareSiteConfigData.init(config);// 初始化数据库中数据
				break;
			}
		} catch (SQLException e) {
			throw new InitDatabaseException("init database error", e);
		}
	}

	public static void initMysql() throws FileNotFoundException, IOException {
		// 生成配置文件
		File configFile = new File(OPENZALY_DATABASE_CONFIG);
		if (!configFile.exists()) {
			writeResourceToFile("/" + OPENZALY_DATABASE_CONFIG, configFile);
		}
		// 加载数据库sql执行脚本
		File fileSql = new File(OPENZALY_MYSQL_SQL);
		if (!fileSql.exists()) {
			writeResourceToFile("/" + OPENZALY_MYSQL_SQL, fileSql);
		}

	}

	public static int upgrade(DBConfig config) throws UpgradeDatabaseException {
		try {
			switch (config.getDb()) {
			case SQLITE:
				return SQLiteUpgrade.upgradeSqliteDB(config);
			case MYSQL:
				throw new UpgradeDatabaseException("database upgrade can't support mysql");
			}
		} catch (SQLException e) {
			throw new UpgradeDatabaseException("upgrade database error", e);
		}
		return 0;
	}

	public static Properties loadDatabaseConfig(String configPath) {
		Properties properties = null;
		InputStream inputStream = null;
		try {
			properties = new Properties();
			inputStream = new FileInputStream(configPath);
			properties.load(inputStream);
		} catch (Exception e) {
			logger.error("load database config error,openzaly will use sqlite database", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error("close db config inputstream error", e);
			}
		}
		return properties;
	}

	private static void writeResourceToFile(String resourceName, File file) throws FileNotFoundException, IOException {
		if (!file.exists()) {
			new FileOutputStream(file).close();
		}
		InputStream is = MysqlManager.class.getResourceAsStream(resourceName);
		BufferedInputStream bis = new BufferedInputStream(is);
		FileOutputStream fos = new FileOutputStream(file);
		try {
			byte[] buffer = new byte[1024];
			int bytesLen = 0;
			while ((bytesLen = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesLen);
			}
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
}
