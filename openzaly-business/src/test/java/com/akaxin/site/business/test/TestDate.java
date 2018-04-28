package com.akaxin.site.business.test;

import java.util.Calendar;
import java.util.UUID;

public class TestDate {

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日

//		System.out.println(year + "" + month + "" + day);
		System.out.println(System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));

	}
}
