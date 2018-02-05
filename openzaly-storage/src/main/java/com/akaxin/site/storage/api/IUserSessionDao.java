package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.akaxin.site.storage.bean.UserSessionBean;

public interface IUserSessionDao {
	public boolean saveUserSession(UserSessionBean bean) throws SQLException;

	public boolean onlineSession(String siteUserId, String deviceId) throws SQLException;

	public boolean offlineSession(String siteUserId, String deviceId) throws SQLException;

	public SimpleAuthBean getUserSession(String sessionId) throws SQLException;

	public List<String> getSessionDeivceIds(String userId) throws SQLException;

	public boolean deleteUserSession(String siteUserId, String deviceId) throws SQLException;
}
