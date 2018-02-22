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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.sqlite.manager.DBConfig;
import com.akaxin.site.storage.sqlite.manager.SQLiteJDBCManager;

/**
 * 数据源初始化管理，不做具体操作对外提供方法
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:15:15
 */
public class DataSourceManager {
	private static Logger logger = LoggerFactory.getLogger(DataSourceManager.class);

	private DataSourceManager() {

	}

	public static void init(DBConfig config) {
		try {
			SQLiteJDBCManager.initSqliteDB(config);
			logger.info("init sqlite datasource finish.");
		} catch (Exception e) {
			logger.error("init sqlite datasource error.", e);
		}
	}
}
