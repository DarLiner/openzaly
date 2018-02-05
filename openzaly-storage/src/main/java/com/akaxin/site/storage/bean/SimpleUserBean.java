package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

/**
 * 用户对象
 * 
 * @author Sam
 * @since 2017.10.25
 *
 */
public class SimpleUserBean {
	private String userId;
	private String userName;
	private String userPhoto;
	private int userStatus;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	public int getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
