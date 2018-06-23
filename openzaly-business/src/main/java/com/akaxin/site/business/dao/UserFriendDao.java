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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.UserProto;
import com.akaxin.site.storage.api.IFriendApplyDao;
import com.akaxin.site.storage.api.IUserFriendDao;
import com.akaxin.site.storage.bean.ApplyFriendBean;
import com.akaxin.site.storage.bean.ApplyUserBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserFriendBean;
import com.akaxin.site.storage.service.FriendApplyDaoService;
import com.akaxin.site.storage.service.UserFriendDaoService;

/**
 * 用户好友相关操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-02-01 17:49:41
 */
public class UserFriendDao {
	private static final Logger logger = LoggerFactory.getLogger(UserFriendDao.class);
	private static UserFriendDao instance = new UserFriendDao();
	private IUserFriendDao userFriendDao = new UserFriendDaoService();
	private IFriendApplyDao friendApplyDao = new FriendApplyDaoService();
	private final int RELATION_NUMBERo = 1;

	public static UserFriendDao getInstance() {
		return instance;
	}

	public int getUserFriendNum(String siteUserId) {
		try {
			return userFriendDao.getUserFriendNum(siteUserId);
		} catch (SQLException e) {
			logger.error("get user friend num error", e);
		}
		return -1;
	}

	public List<SimpleUserBean> getUserFriends(String userId) {
		List<SimpleUserBean> friendList = new ArrayList<SimpleUserBean>();
		try {
			friendList = userFriendDao.getUserFriends(userId);
		} catch (SQLException e) {
			logger.error("get user friend list error", e);
		}
		return friendList;
	}

	public List<SimpleUserBean> getUserFriendsByPage(String siteUserId, int pageNum, int pageSize) {
		List<SimpleUserBean> friendList = null;
		try {
			friendList = userFriendDao.getUserFriendsByPage(siteUserId, pageNum, pageSize);
		} catch (SQLException e) {
			logger.error("get user friend list error", e);
		}
		return friendList;
	}

	public boolean saveFriendApply(String siteUserId, String siteFriendId, String applyReason) {
		try {
			return friendApplyDao.saveApply(siteFriendId, siteUserId, applyReason);
		} catch (SQLException e) {
			logger.error("friend apply error.", e);
		}
		return false;
	}

	// 查询二者是否为好友
	public boolean isFriend(String siteUserId, String siteFriendId) throws SQLException {
		return userFriendDao.queryIsFriendRelation(siteUserId, siteFriendId);
	}

	public boolean isNotFriend(String siteUserId, String siteFriendId) throws SQLException {
		return !isFriend(siteUserId, siteFriendId);
	}

	public boolean agreeApply(String siteUserId, String siteFriendId, boolean agree) {
		boolean result = true;
		try {
			if (agree) {
				if (userFriendDao.queryRelation(siteUserId, siteFriendId) != RELATION_NUMBERo) {
					result = userFriendDao.saveRelation(siteUserId, siteFriendId, RELATION_NUMBERo);
				}
				if (userFriendDao.queryRelation(siteFriendId, siteUserId) != RELATION_NUMBERo) {
					result = userFriendDao.saveRelation(siteFriendId, siteUserId, RELATION_NUMBERo) && result;
				}
			}
		} catch (Exception e) {
			result = false;
			logger.error("agree friend apply error.", e);
		}
		return result;
	}

	public ApplyFriendBean agreeApplyWithClear(String siteUserId, String siteFriendId, boolean isMaster) {
		ApplyFriendBean bean = null;
		try {
			bean = friendApplyDao.getApplyInfo(siteUserId, siteFriendId, isMaster);
			friendApplyDao.deleteApply(siteFriendId, siteUserId);
			friendApplyDao.deleteApply(siteUserId, siteFriendId);
		} catch (SQLException e) {
			logger.error("agree apply friend with clear error", e);
		}
		return bean;
	}

	public UserProto.UserRelation getUserRelation(String siteUserId, String siteFriendId) {
		int relation = 0;
		try {
			relation = userFriendDao.queryRelation(siteUserId, siteFriendId);
			if (relation == RELATION_NUMBERo) {
				relation = userFriendDao.queryRelation(siteFriendId, siteUserId);
				if (relation == RELATION_NUMBERo) {
					return UserProto.UserRelation.RELATION_FRIEND;
				}
				return UserProto.UserRelation.RELATION_CARE;
			} else {
				relation = userFriendDao.queryRelation(siteFriendId, siteUserId);
				if (relation == RELATION_NUMBERo) {
					return UserProto.UserRelation.RELATION_FANS;
				}
			}
		} catch (SQLException e) {
			logger.error("get user relation error.siteUserId={} siteFriendId={}", e);
		}
		return UserProto.UserRelation.RELATION_NONE;
	}

	/**
	 * 获取一个好友的申请总数
	 * 
	 * @param siteUserId
	 * @param siteFriendId
	 * @return
	 */
	public int getApplyCount(String siteUserId, String siteFriendId) {
		int count = 0;
		try {
			count = friendApplyDao.getApplyCount(siteUserId, siteFriendId);
		} catch (SQLException e) {
			logger.error("get apply user count error.", e);
		}
		return count;
	}

	/**
	 * 获取用户当前的好友申请总数
	 * 
	 * @param siteUserId
	 * @return
	 */
	public int getApplyCount(String siteUserId) {
		int count = 0;
		try {
			count = friendApplyDao.getApplyCount(siteUserId);
		} catch (SQLException e) {
			logger.error("get apply user count error.", e);
		}
		return count;
	}

	public List<ApplyUserBean> getApplyUserList(String siteUserId) {
		try {
			return friendApplyDao.getApplyUsers(siteUserId);
		} catch (SQLException e) {
			logger.error("get apply user list error.", e);
		}
		return null;
	}

	public boolean deleteFriend(String siteUserId, String siteFriendId) {
		boolean result = false;
		try {
			result = userFriendDao.deleteRelation(siteUserId, siteFriendId);
		} catch (SQLException e) {
			logger.error("delete friend error.", e);
		}
		return result;
	}

	public UserFriendBean getFriendSetting(String siteUserId, String siteFriendId) {
		try {
			UserFriendBean bean = userFriendDao.getFriendSetting(siteUserId, siteFriendId);
			if (bean == null) {
				bean = new UserFriendBean();
				bean.setMute(false);
			}
			return bean;
		} catch (SQLException e) {
			logger.error("get friend setting error", e);
		}
		return null;
	}

	public boolean updateFriendSetting(String siteUserId, UserFriendBean bean) {
		try {
			return userFriendDao.updateFriendSetting(siteUserId, bean);
		} catch (SQLException e) {
			logger.error("update friend setting error", e);
		}
		return false;
	}

	public boolean getFriendMute(String siteUserId, String siteFriendId) throws SQLException {
		return userFriendDao.isMute(siteUserId, siteFriendId);
	}

	public boolean updateFriendMute(String siteUserId, String siteFriendId, boolean mute) {
		try {
			return userFriendDao.updateMute(siteUserId, siteFriendId, mute);
		} catch (SQLException e) {
			logger.error("update friend mute error", e);
		}
		return false;
	}

	public boolean remarkFriend(String siteUserId, String siteFriendId, String aliasName, String aliasInLatin) {
		try {
			return userFriendDao.remarkFriend(siteUserId, siteFriendId, aliasName, aliasInLatin);
		} catch (SQLException e) {
			logger.error("update friend mute error", e);
		}
		return false;
	}
}
