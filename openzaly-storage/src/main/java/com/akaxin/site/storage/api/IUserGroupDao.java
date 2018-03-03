package com.akaxin.site.storage.api;

import java.sql.SQLException;

import com.akaxin.site.storage.bean.UserGroupBean;

public interface IUserGroupDao {

	UserGroupBean getUserGroupSetting(String siteUserId, String siteGroupId) throws SQLException;

	boolean updateUserGroupSetting(String siteUserId, UserGroupBean bean) throws SQLException;

}
