package com.zaly.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.akaxin.common.http.ZalyHttpClient;
import com.akaxin.common.utils.GsonUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpClient {
	private static OkHttpClient client = new OkHttpClient();

	public static void main(String args[]) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "aaaaa");
		byte[] body = ZalyHttpClient.getInstance().postString("http://119.27.185.209:516/siteMember/applyAddFriend", "hello");
		System.out.println("body=" + new String(body));

		String url = "http://119.27.185.209:516/siteMember/applyAddFriend";
		postKV(url);

		postJson(url, GsonUtils.toJson(map));
		postJson(url, map.toString());
	}

	static String postJson(String url, String json) throws IOException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody postBody = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(postBody).build();
		Response response = client.newCall(request).execute();
		System.out.println("post postJson response =" + response.isSuccessful());
		if (response.isSuccessful()) {
			return response.body().toString();
		} else {
			System.out.println("http post failed");
			throw new IOException("post json Unexpected code " + response);
		}
	}

	static String postKV(String url) throws IOException {

		RequestBody formBody = new FormEncodingBuilder().add("platform", "android").add("name", "bug").build();

		Request request = new Request.Builder().url(url).post(formBody).build();

		Response response = client.newCall(request).execute();
		System.out.println("post KV response =" + response.isSuccessful());
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}
}