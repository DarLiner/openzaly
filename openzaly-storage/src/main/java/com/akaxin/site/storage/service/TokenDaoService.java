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
package com.akaxin.site.storage.service;

import java.sql.SQLException;

import com.akaxin.site.storage.api.ITokenDao;
import com.akaxin.site.storage.bean.ExpireToken;
import com.akaxin.site.storage.dao.SiteTokenDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-28 16:42:43
 */
public class TokenDaoService implements ITokenDao {

	@Override
	public boolean addToken(ExpireToken bean) throws SQLException {
		return SiteTokenDao.getInstance().addToken(bean);
	}

	@Override
	public ExpireToken getExpireToken(String token) throws SQLException {
		return SiteTokenDao.getInstance().queryExpireToken(token);
	}

	@Override
	public ExpireToken getExpireTokenByBid(String bid, long time) throws SQLException {
		return SiteTokenDao.getInstance().queryExpireTokenByBid(bid, time);
	}

}
