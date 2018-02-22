package com.akaxin.site.boot.test;

import java.util.ArrayList;
import java.util.List;

import com.akaxin.site.boot.config.SiteDefaultIcon;

public class SubStrTest {
	public static void main(String[] args) {
		String testStr = SiteDefaultIcon.DEFAULT_SITE_ADMIN_ICON;
		
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < testStr.length(); i += 100) {
			if (i + 100 > testStr.length()) {
				list.add(testStr.substring(i, testStr.length()));
			} else {
				list.add(testStr.substring(i, i + 100));
			}
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println("+\"" + list.get(i)+"\"");

		}
	}
}
