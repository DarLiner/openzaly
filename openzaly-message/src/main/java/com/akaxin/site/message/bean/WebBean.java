package com.akaxin.site.message.bean;

import com.akaxin.common.utils.GsonUtils;

public class WebBean {
	private String webCode;
	private int width;
	private int height;

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

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
