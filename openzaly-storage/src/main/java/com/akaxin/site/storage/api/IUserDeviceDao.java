package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.UserDeviceBean;

public interface IUserDeviceDao {

	boolean saveUserDevice(UserDeviceBean bean) throws SQLException;

	boolean updateUserDevice(UserDeviceBean bean) throws SQLException;

	boolean updateActiveTime(String siteUserId, String deviceId) throws SQLException;

	public UserDeviceBean getDeviceDetails(String siteUserId, String deviceId) throws SQLException;

	public String getDeviceId(String siteUserId, String devicePuk) throws SQLException;

	public UserDeviceBean getLatestDevice(String siteUserId) throws SQLException;

	public List<UserDeviceBean> getUserDeviceList(String siteUserId) throws SQLException;

	public List<UserDeviceBean> getActiveDeviceList(String siteUserId) throws SQLException;

	public String getUserToken(String siteUserId) throws SQLException;

	public int limirDeviceNum(String siteUserId) throws SQLException;
}
