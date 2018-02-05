package com.akaxin.site.storage.bean;

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

	public String toString() {
		return "id=" + id + ",siteUserId=" + siteUserId + ",siteGroupId=" + siteGroupId + ",userRole=" + userRole
				+ ",tsKey=" + tsKey + ",deviceId" + deviceId + ",addTime=" + addTime;

	}

}
