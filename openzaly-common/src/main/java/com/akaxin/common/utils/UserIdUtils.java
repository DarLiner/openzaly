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

import java.util.UUID;
import java.util.zip.CRC32;

import com.akaxin.common.crypto.HashCrypto;

/**
 * <pre>
 * 		生产各种不同类型的用户ID
 * 		siteUserId && GlobalUserId
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-26 15:38:49
 */
public class UserIdUtils {
	// 用户在每个站点的id为UUID
	public static String createSiteUserId() {
		return UUID.randomUUID().toString();
	}

	// 通过用户公钥，生成用户
	public static String getV1GlobalUserId(String userIdPubk) {
		String body = userIdPubk;
		String SHA1UserPubKey = HashCrypto.SHA1(body);
		CRC32 c32 = new CRC32();
		c32.update(body.getBytes(), 0, body.getBytes().length);
		String CRC32UserPubKey = String.valueOf(c32.getValue());
		return SHA1UserPubKey + "-" + CRC32UserPubKey;
	}
}
