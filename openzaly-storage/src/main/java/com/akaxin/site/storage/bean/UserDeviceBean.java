package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

/**
 * 用户设备信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.21
 */
public class UserDeviceBean {
	private String siteUserId;
	private String deviceId;
	private String userDevicePubk;
	private String deviceName;
	private String deviceIp;
	private String userToken;
	private long loginTime;
	private long activeTime;
	private long addTime;

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserDevicePubk() {
		return userDevicePubk;
	}

	public void setUserDevicePubk(String userDevicePubk) {
		this.userDevicePubk = userDevicePubk;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public long getLoginTime() {
		return loginTime;
	}
	
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
