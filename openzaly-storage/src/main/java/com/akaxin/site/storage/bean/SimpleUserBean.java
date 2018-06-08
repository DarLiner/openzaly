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
	protected String siteUserId;// siteUserId
	private String siteLoginId;// loginId 登陆账号
	private String userName;// 用户名,昵称nickname
	private String userNameInLatin;
	private String aliasName;// 备注
	private String aliasNameInLatin;// 备注拼音
	private String userPhoto;
	private int userStatus;

	public String getUserId() {
		return siteUserId;
	}

	public void setUserId(String siteUserId) {
		this.siteUserId = siteUserId;
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

	public String getSiteLoginId() {
		return siteLoginId;
	}

	public void setSiteLoginId(String siteLoginId) {
		this.siteLoginId = siteLoginId;
	}

	public String getUserNameInLatin() {
		return userNameInLatin;
	}

	public void setUserNameInLatin(String userNameInLatin) {
		this.userNameInLatin = userNameInLatin;
	}

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getAliasNameInLatin() {
		return aliasNameInLatin;
	}

	public void setAliasNameInLatin(String aliasNameInLatin) {
		this.aliasNameInLatin = aliasNameInLatin;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
