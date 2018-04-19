package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.SimpleUserRelationBean;
import com.akaxin.site.storage.bean.UserProfileBean;

public interface IUserProfileDao {

    public boolean saveUserProfile(UserProfileBean bean) throws SQLException;

    public String getSiteUserId(String userIdPubk) throws SQLException;

    public String getGlobalUserId(String userId) throws SQLException;

    public SimpleUserBean getSimpleProfileById(String userId) throws SQLException;

    public SimpleUserBean getSimpleProfileByPubk(String userId) throws SQLException;

    public List<SimpleUserBean> getSimpleProfileByName(String userName) throws SQLException;

    public UserProfileBean getUserProfileById(String userId) throws SQLException;

    public UserProfileBean getUserProfileByGlobalUserId(String userId) throws SQLException;

    public UserProfileBean getUserProfileByPubk(String userIdPubk) throws SQLException;

    public int updateUserProfile(UserProfileBean userBean) throws SQLException;

    public int updateUserStatus(String siteUserId, int status) throws SQLException;

    public List<SimpleUserRelationBean> getUserRelationPageList(String siteUserId, int pageNum, int pageSize)
            throws SQLException;

    public List<SimpleUserBean> getUserPageList(int pageNum, int pageSize) throws SQLException;

    public boolean isMute(String userId) throws SQLException;

    public boolean updateMute(String userId, boolean mute) throws SQLException;

    public int queryNumRegisterPerDay(long now, int day) throws SQLException;

    int getUserNum() throws SQLException;
}
