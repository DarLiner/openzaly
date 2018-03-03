package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

/**
 * 用户对应群的bean
 * 
 * @author anguoyue
 *
 */
public class UserGroupBean {
	private String id;
	private String siteUserId;
	private String siteGroupId;
	private String userRole;
	private String tsKey;
	private String deviceId;
	private boolean mute;
	private long addTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getSiteGroupId() {
		return siteGroupId;
	}

	public void setSiteGroupId(String siteGroupId) {
		this.siteGroupId = siteGroupId;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getTsKey() {
		return tsKey;
	}

	public void setTsKey(String tsKey) {
		this.tsKey = tsKey;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
