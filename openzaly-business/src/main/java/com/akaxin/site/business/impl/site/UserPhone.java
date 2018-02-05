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
package com.akaxin.site.business.impl.site;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.platform.ApiPhoneConfirmTokenProto;
import com.akaxin.site.message.push.WritePackage;

/**
 * 当注册方式为：实名用户，此时需要向平台验证手机号码是否正确
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-14 22:30:25
 */
public class UserPhone {
	private static final Logger logger = LoggerFactory.getLogger(UserPhone.class);

	private UserPhone() {

	}

	static class SingletonHolder {
		private static UserPhone instance = new UserPhone();
	}

	public static UserPhone getInstance() {
		return SingletonHolder.instance;
	}

	public String getPhoneIdFromPlatform(String phoneToken) {
		try {
			ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest request = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest
					.newBuilder().setPhoneToken(phoneToken).build();
			logger.info("实名认证 获取用户手机信息 : phoneToken={} {}", phoneToken, request.getPhoneToken());
			byte[] responseBytes = WritePackage.getInstance().syncWrite(CommandConst.API_PHONE_CONFIRETOKEN,
					request.toByteArray());

			if (responseBytes != null) {
				ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse resposne = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse
						.parseFrom(responseBytes);
				String phoneCode = resposne.getGlobalRoaming() + resposne.getPhoneId();
				logger.info("get phoncode={} from platform", phoneCode);
				return phoneCode;
			}
		} catch (Exception e) {
			logger.error("get phoneid from platform error.", e);
		}

		return null;
	}

}
