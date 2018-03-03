package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class UserFriendBean extends UserProfileBean {
	private boolean mute;// 是否对该用户设置消息免打扰

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
