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
package com.akaxin.common.crypto;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * SHA1 && MD5
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:30:50
 */
public class HashCrypto {

	public static String SHA1(String key) {
		byte[] data = DigestUtils.sha1(key);
		return Hex.encodeHexString(data);
	}

	public static byte[] SHA1Bytes(String key) {
		return DigestUtils.sha1(key);
	}

	public static String MD5(String key) {
		byte[] md5Data = DigestUtils.md5(key);
		return Hex.encodeHexString(md5Data);
	}

	public static byte[] MD5Bytes(String key) {
		return DigestUtils.md5(key);
	}

}