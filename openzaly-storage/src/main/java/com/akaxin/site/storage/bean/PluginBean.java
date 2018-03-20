package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class PluginBean {
	private int id;
	private String name;
	private String urlPage;
	private String apiUrl;
	private String icon;
	private String authKey;
	private String allowedIp;
	private int position;// 扩展的位置，首页还是消息帧
	private int sort;// 排序字段
	private int displayMode;// 在客户端展示的方式
	private int permissionStatus;// 是否可用
	private long addTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlPage() {
		return urlPage;
	}

	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getAllowedIp() {
		return allowedIp;
	}

	public void setAllowedIp(String allowedIp) {
		this.allowedIp = allowedIp;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public int getPermissionStatus() {
		return permissionStatus;
	}

	public void setPermissionStatus(int permissionStatus) {
		this.permissionStatus = permissionStatus;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
