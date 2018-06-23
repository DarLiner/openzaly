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

import com.akaxin.site.storage.api.IFriendApplyDao;
import com.akaxin.site.storage.bean.ApplyFriendBean;
import com.akaxin.site.storage.bean.ApplyUserBean;
import com.akaxin.site.storage.dao.SiteFriendApplyDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:12:28
 */
public class FriendApplyDaoService implements IFriendApplyDao {

	@Override
	public boolean saveApply(String siteUserId, String siteFriendId, String applyReason) throws SQLException {
		return SiteFriendApplyDao.getInstance().saveApply(siteUserId, siteFriendId, applyReason);
	}

	@Override
	public boolean deleteApply(String siteUserId, String siteFriendId) throws SQLException {
		return SiteFriendApplyDao.getInstance().deleteApply(siteUserId, siteFriendId);
	}

	@Override
	public int getApplyCount(String siteUserId, String siteFriendId) throws SQLException {
		return SiteFriendApplyDao.getInstance().getApplyCount(siteUserId, siteFriendId);
	}

	@Override
	public int getApplyCount(String siteUserId) throws SQLException {
		return SiteFriendApplyDao.getInstance().getApplyCount(siteUserId);
	}

	@Override
	public ApplyFriendBean getApplyInfo(String siteUserId, String siteFriendId, boolean isMaster) throws SQLException {
		return SiteFriendApplyDao.getInstance().getApplyInfo(siteUserId, siteFriendId, isMaster);
	}

	@Override
	public List<ApplyUserBean> getApplyUsers(String siteUserId) throws SQLException {
		return SiteFriendApplyDao.getInstance().queryApplyUsers(siteUserId);
	}

}
