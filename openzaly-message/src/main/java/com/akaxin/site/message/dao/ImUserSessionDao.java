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
package com.akaxin.site.message.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.service.UserSessionDaoService;

public class ImUserSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(ImUserSessionDao.class);
	private static ImUserSessionDao instance = new ImUserSessionDao();
	private IUserSessionDao userSessionDao = new UserSessionDaoService();

	public static ImUserSessionDao getInstance() {
		return instance;
	}

	public List<String> getSessionDevices(String siteUserId) {
		List<String> sessionDevices = new ArrayList<String>();
		try {
			sessionDevices = userSessionDao.getSessionDeivceIds(siteUserId);
		} catch (SQLException e) {
			logger.error("get session devices error.", e);
		}
		return sessionDevices;
	}

}
