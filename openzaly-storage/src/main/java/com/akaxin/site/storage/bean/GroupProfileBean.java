package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class GroupProfileBean {
	private int id;
	private String groupId;
	private String groupName;
	private String groupPhoto;
	private String groupNotice;
	private int tsStatus;
	private int groupStatus;
	private String createUserId;
	private long createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupPhoto() {
		return groupPhoto;
	}

	public void setGroupPhoto(String groupPhoto) {
		this.groupPhoto = groupPhoto;
	}

	public String getGroupNotice() {
		return groupNotice;
	}

	public void setGroupNotice(String groupNotice) {
		this.groupNotice = groupNotice;
	}

	public int getTsStatus() {
		return tsStatus;
	}

	public void setTsStatus(int tsStatus) {
		this.tsStatus = tsStatus;
	}

	public int getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
