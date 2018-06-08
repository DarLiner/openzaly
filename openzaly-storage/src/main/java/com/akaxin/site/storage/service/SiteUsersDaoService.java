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
import java.util.List;

import com.akaxin.site.storage.api.ISiteUsersDao;
import com.akaxin.site.storage.dao.SiteUsersDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:03
 */
public class SiteUsersDaoService implements ISiteUsersDao {

	@Override
	public List<String> getSiteUserByPage(int pageNum, int pageSize) throws SQLException {
		return SiteUsersDao.getInstance().querySiteUserId(pageNum, pageSize);
	}

}
