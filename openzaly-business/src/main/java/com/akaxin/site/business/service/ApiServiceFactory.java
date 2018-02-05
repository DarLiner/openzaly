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
package com.akaxin.site.business.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.constant.RequestAction;
import com.akaxin.site.business.impl.IRequestService;
import com.akaxin.site.business.impl.tai.ApiDeviceService;
import com.akaxin.site.business.impl.tai.ApiFileService;
import com.akaxin.site.business.impl.tai.ApiFriendService;
import com.akaxin.site.business.impl.tai.ApiGroupService;
import com.akaxin.site.business.impl.tai.ApiPluginService;
import com.akaxin.site.business.impl.tai.ApiSecretChatService;
import com.akaxin.site.business.impl.tai.ApiSiteService;
import com.akaxin.site.business.impl.tai.ApiUserService;

/**
 * API业务请求，分发工厂
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.24 18:25:31
 */
public class ApiServiceFactory {
	private static final Logger logger = LoggerFactory.getLogger(ApiServiceFactory.class);

	public static IRequestService getService(String serviceName) {
		RequestAction nameEnum = RequestAction.getAction(serviceName);
		switch (nameEnum) {
		case SITE:
			return new ApiSiteService();
		case API_USER:
			return new ApiUserService();
		case API_FRIEND:
			return new ApiFriendService();
		case API_GROUP:
			return new ApiGroupService();
		case API_SECRETCHAT:
			return new ApiSecretChatService();
		case API_FILE:
			return new ApiFileService();
		case API_DEVICE:
			return new ApiDeviceService();
		case API_PLUGIN:
			return new ApiPluginService();
		default:
			logger.info("api business service error.service={}", nameEnum.getName());
			break;
		}
		return null;
	}

}
