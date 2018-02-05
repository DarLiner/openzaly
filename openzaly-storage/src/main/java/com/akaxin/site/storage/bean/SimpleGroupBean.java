package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

/**
 * 群对象简单bean
 * 
 * @author Sam
 * @since 2017.10.25
 *
 */
public class SimpleGroupBean {
	
	private String groupId;
	private String groupName;
	private String groupPhoto;
	
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

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
