package com.akaxin.site.storage.bean;

public class SiteConfigBean {
	private String siteIp;
	private int sitePort;
	private String siteName;
	private String siteIcon;
	private String siteInstructions;

	public String getSiteIp() {
		return siteIp;
	}

	public void setSiteIp(String siteIp) {
		this.siteIp = siteIp;
	}

	public int getSitePort() {
		return sitePort;
	}

	public void setSitePort(int sitePort) {
		this.sitePort = sitePort;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteIcon() {
		return siteIcon;
	}

	public void setSiteIcon(String siteIcon) {
		this.siteIcon = siteIcon;
	}

	public String getSiteInstructions() {
		return siteInstructions;
	}

	public void setSiteInstructions(String siteInstructions) {
		this.siteInstructions = siteInstructions;
	}

}
