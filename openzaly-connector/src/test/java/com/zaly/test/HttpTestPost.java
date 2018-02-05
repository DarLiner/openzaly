package com.zaly.test;

import java.io.IOException;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpTestPost {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	static OkHttpClient client = new OkHttpClient();

	static String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		System.out.println("response = " + response.isSuccessful());
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

	public static void main(String args[]) {
		String url = "http://119.27.185.209:516/siteMember/applyAddFriend";
		try {
			post(url, "hello");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
