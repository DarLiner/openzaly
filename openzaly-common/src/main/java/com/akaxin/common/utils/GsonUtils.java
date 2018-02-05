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
package com.akaxin.common.utils;

import com.google.gson.Gson;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:34:31
 */
public class GsonUtils {
	public static String toJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static <T> T fromJson(String str, Class<T> clazz) {
		return new Gson().fromJson(str, clazz);
	}
}
