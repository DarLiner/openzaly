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
import com.akaxin.site.business.bean.PlatformPhoneBean;
import com.akaxin.site.message.platform.PlatformClient;

/**
 * 当注册方式为：实名用户，此时需要向平台验证手机号码是否正确
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-14 22:30:25
 */
public class PlatformUserPhone {
	private static final Logger logger = LoggerFactory.getLogger(PlatformUserPhone.class);

	private PlatformUserPhone() {

	}

	static class SingletonHolder {
		private static PlatformUserPhone instance = new PlatformUserPhone();
	}

	public static PlatformUserPhone getInstance() {
		return SingletonHolder.instance;
	}

	public PlatformPhoneBean getPhoneIdFromPlatform(String phoneToken) {
		try {
			PlatformPhoneBean bean = null;
			ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest request = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest
					.newBuilder().setPhoneToken(phoneToken).build();
			logger.debug("realname get phone from platform : phoneToken={} {}", phoneToken, request.getPhoneToken());

			byte[] responseBytes = PlatformClient.syncWrite(CommandConst.API_PHONE_CONFIRETOKEN, request.toByteArray());

			if (responseBytes != null) {
				bean = new PlatformPhoneBean();
				ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse resposne = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse
						.parseFrom(responseBytes);
				bean.setPhoneId(resposne.getPhoneId());
				bean.setCountryCode(resposne.getCountryCode());
				bean.setUserIdPubk(resposne.getUserIdPubk());
				logger.debug("get user phoneBean={} from platform", bean);
				return bean;
			}
		} catch (Exception e) {
			logger.error("get phoneid from platform error.", e);
		}

		return null;
	}

}
