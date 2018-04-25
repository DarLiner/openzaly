package com.akaxin.admin.site.bean;

import com.akaxin.common.utils.GsonUtils;

public class WebMessageBean {
	private String siteUserId;
	private String siteFriendId;
	private String siteGroupId;
	private String msgId;
	private String webCode;
	private int width;
	private int height;
	private long msgTime;

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

	public String getWebCode() {
		return webCode;
	}

	public void setWebCode(String webCode) {
		this.webCode = webCode;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public long getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(long msgTime) {
		this.msgTime = msgTime;
	}

	public String getSiteGroupId() {
		return siteGroupId;
	}

	public void setSiteGroupId(String siteGroupId) {
		this.siteGroupId = siteGroupId;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
