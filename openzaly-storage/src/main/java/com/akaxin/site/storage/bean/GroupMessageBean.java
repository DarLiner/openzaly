package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class GroupMessageBean {
	private int id;
	private String siteGroupId;
	private String msgId;
	private String sendUserId;
	private String sendDeviceId;
	private int msgType;
	private String content;
	private long msgTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSiteGroupId() {
		return siteGroupId;
	}

	public void setSiteGroupId(String siteGroupId) {
		this.siteGroupId = siteGroupId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getSendDeviceId() {
		return sendDeviceId;
	}

	public void setSendDeviceId(String sendDeviceId) {
		this.sendDeviceId = sendDeviceId;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(long msgTime) {
		this.msgTime = msgTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
