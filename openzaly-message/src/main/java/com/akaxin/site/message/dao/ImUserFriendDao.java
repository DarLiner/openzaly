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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUserFriendDao;
import com.akaxin.site.storage.service.UserFriendDaoService;

/**
 * IM消息通信过程，判断用户之间的关系操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-20 11:51:17
 */
public class ImUserFriendDao {
	private static final Logger logger = LoggerFactory.getLogger(ImUserFriendDao.class);
	private static ImUserFriendDao instance = new ImUserFriendDao();
	private IUserFriendDao userFriendDao = new UserFriendDaoService();

	public static ImUserFriendDao getInstance() {
		return instance;
	}

	public boolean isFriend(String siteUserId, String siteFriendId) {
		try {
			int userRel = userFriendDao.queryRelation(siteUserId, siteFriendId);
			int friendRel = userFriendDao.queryRelation(siteFriendId, siteUserId);
			return userRel == 1 && friendRel == 1;
		} catch (SQLException e) {
			logger.error("query friend relation error.", e);
		}
		return false;
	}

	/**
	 * 是否设置了消息免打扰功能,默认是静音状态
	 * 
	 * @return
	 */
	public boolean isMesageMute(String siteUserId, String siteFriendId) {
		try {
			return userFriendDao.isMute(siteUserId, siteFriendId);
		} catch (SQLException e) {
			logger.error("query message mutet status error", e);
		}
		return true;
	}
}
