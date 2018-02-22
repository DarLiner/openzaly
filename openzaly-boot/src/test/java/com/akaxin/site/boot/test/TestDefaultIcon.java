package com.akaxin.site.boot.test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.akaxin.site.boot.config.SiteDefaultIcon;
import com.akaxin.site.business.utils.FileServerUtils;

public class TestDefaultIcon {
	public static void main(String args[]) {
		// String fileUrl = "site-admin-icon.jpg";
		String fileUrl = "friend-square-icon.jpg";
		byte[] fileBytes = FileServerUtils.fileToBinary(fileUrl);

		String str = Base64.getEncoder().encodeToString(fileBytes);
		System.out.println(str);
		

	}
}
