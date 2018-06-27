/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.common.http;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

public class ZalyHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(ZalyHttpClient.class);
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final OkHttpClient httpClient = new OkHttpClient();

	private static ZalyHttpClient instance = new ZalyHttpClient();

	private ZalyHttpClient() {
	}

	public static ZalyHttpClient getInstance() {
		return instance;
	}

	public byte[] get(String url) throws Exception {
		ResponseBody body = null;
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = httpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				body = response.body();
				byte[] res = body.bytes();
				return res;
			} else {
				logger.error("http get url={} error.{}", url, response.message());
			}
		} finally {
			if (body != null) {
				body.close();
			}
		}
		return null;
	}

	public byte[] postString(String url, String json) throws IOException {
		ResponseBody body = null;
		try {
			RequestBody postBody = RequestBody.create(JSON, json);
			Request request = new Request.Builder().url(url).post(postBody).build();
			Response response = httpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				body = response.body();
				byte[] res = body.bytes();
				return res;
			} else {
				logger.error("http post error.{}", response.message());
			}
		} finally {
			if (body != null) {
				body.close();
			}
		}
		return null;
	}

	public byte[] postBytes(String url, byte[] bytes) throws IOException {
		ResponseBody body = null;
		try {
			RequestBody postBody = RequestBody.create(JSON, bytes);
			Request request = new Request.Builder().url(url).post(postBody).build();
			Response response = httpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				body = response.body();
				byte[] res = body.bytes();
				return res;
			} else {
				logger.error("http post error.{}", response.message());
			}
		} finally {
			if (body != null) {
				body.close();
			}
		}
		return null;
	}
}
