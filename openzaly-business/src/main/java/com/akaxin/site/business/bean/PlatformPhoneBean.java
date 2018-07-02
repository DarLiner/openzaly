package com.akaxin.site.business.bean;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.common.utils.GsonUtils;

public class PlatformPhoneBean {

	private String phoneId;
	private String countryCode;
	private String userIdPubk;

	/**
	 * @return the phoneId
	 */
	public String getPhoneId() {
		return phoneId;
	}

	/**
	 * @param phoneId
	 *            the phoneId to set
	 */
	public void setPhoneId(String phoneId) {
		this.phoneId = phoneId;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode
	 *            the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the countryCode:phoneId
	 */
	public String getFullPhoneId() {
		if (StringUtils.isNotEmpty(this.countryCode)) {
			return this.countryCode + ":" + phoneId;
		}
		return "+86:" + phoneId;
	}

	/**
	 * @return the userIdPubk
	 */
	public String getUserIdPubk() {
		return userIdPubk;
	}

	/**
	 * @param userIdPubk
	 *            the userIdPubk to set
	 */
	public void setUserIdPubk(String userIdPubk) {
		this.userIdPubk = userIdPubk;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
