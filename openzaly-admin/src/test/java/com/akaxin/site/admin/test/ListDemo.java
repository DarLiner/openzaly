package com.akaxin.site.admin.test;

import java.util.Arrays;
import java.util.List;

import com.akaxin.common.utils.GsonUtils;

public class ListDemo {
	public static void main(String[] args) {
		List<String> testList = Arrays.asList("A", "B", "C");

		String hello = GsonUtils.toJson(testList);

		System.out.println(hello);

		List<String> test2 = GsonUtils.fromJson(testList.toString(), List.class);
		System.out.println(test2);

	}
}
