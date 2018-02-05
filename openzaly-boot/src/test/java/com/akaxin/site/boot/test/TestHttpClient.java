package com.akaxin.site.boot.test;

import java.util.HashMap;
import java.util.Map;

import com.akaxin.common.http.ZalyHttpClient;
import com.akaxin.common.utils.GsonUtils;

public class TestHttpClient {
	public static void main(String args[]) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "aaaaa");
		byte[] body = ZalyHttpClient.getInstance().postString("http://localhost:8080/hai/user/queryList", GsonUtils.toJson(map));
		System.out.println("body=" + new String(body));
	}
}