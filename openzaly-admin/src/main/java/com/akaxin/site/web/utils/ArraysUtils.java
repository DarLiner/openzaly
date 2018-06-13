package com.akaxin.site.web.utils;

import java.util.ArrayList;
import java.util.List;

public class ArraysUtils {

	public static List<String> asList(String[] objs) {
		if (objs == null)
			return null;
		List<String> list = new ArrayList<String>();
		for (String obj : objs) {
			list.add(obj);
		}
		return list;
	}

}
