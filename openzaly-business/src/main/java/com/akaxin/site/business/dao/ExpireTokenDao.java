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
package com.akaxin.site.business.dao;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.ITokenDao;
import com.akaxin.site.storage.bean.ExpireToken;
import com.akaxin.site.storage.service.TokenDaoService;

/**
 * 站点相关过期token操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-28 16:57:56
 */
public class ExpireTokenDao {
	private static final Logger logger = LoggerFactory.getLogger(ExpireTokenDao.class);

	private ITokenDao tokenDao = new TokenDaoService();
	private static ExpireTokenDao instance = new ExpireTokenDao();

	public static ExpireTokenDao getInstance() {
		return instance;
	}

	/**
	 * 新增扩展
	 * 
	 * @param bean
	 * @return
	 */
	public boolean addToken(ExpireToken bean) {
		try {
			return tokenDao.addToken(bean);
		} catch (SQLException e) {
			logger.error("add expire token error.", e);
		}
		return false;
	}

	public ExpireToken getExpireToken(String token) {
		try {
			return tokenDao.getExpireToken(token);
		} catch (SQLException e) {
			logger.error("add expire token error.", e);
		}
		return null;
	}

	public ExpireToken getExpireTokenByBid(String bid, long time) {
		try {
			return tokenDao.getExpireTokenByBid(bid, time);
		} catch (SQLException e) {
			logger.error("get expire token by bid and time error.", e);
		}
		return null;
	}

}
