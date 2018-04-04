package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class ApplyFriendBean {
	private String siteUserId;
	private String siteFriendId;
	private String applyInfo;
	private long applyTime;

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getSiteFriendId() {
		return siteFriendId;
	}

	public void setSiteFriendId(String siteFriendId) {
		this.siteFriendId = siteFriendId;
	}

	public String getApplyInfo() {
		return applyInfo;
	}

	public void setApplyInfo(String applyInfo) {
		this.applyInfo = applyInfo;
	}

	public long getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(long applyTime) {
		this.applyTime = applyTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
