package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class U2MessageBean {
	private int id;
	private String siteUserId;
	private String msgId;
	private String sendUserId;
	private int msgType;
	private String content;
	private String deviceId;
	private String tsKey;
	private long msgTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getTsKey() {
		return tsKey;
	}

	public void setTsKey(String tsKey) {
		this.tsKey = tsKey;
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
