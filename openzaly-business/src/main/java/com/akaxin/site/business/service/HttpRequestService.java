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

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.HttpUriAction;
import com.akaxin.site.business.api.IRequest;
import com.akaxin.site.business.impl.hai.HttpFriendService;
import com.akaxin.site.business.impl.hai.HttpGroupService;
import com.akaxin.site.business.impl.hai.HttpMessageService;
import com.akaxin.site.business.impl.hai.HttpPushService;
import com.akaxin.site.business.impl.hai.HttpSessionService;
import com.akaxin.site.business.impl.hai.HttpSiteService;
import com.akaxin.site.business.impl.hai.HttpUserService;

/**
 * Http请求服务分发
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018.1.1
 *
 */
public class HttpRequestService implements IRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestService.class);

	public CommandResponse process(Command command) {
		// 路由分发
		HttpUriAction huaEnum = HttpUriAction.getUriActionEnum(command.getRety(), command.getService());
		CommandResponse response = null;
		try {
			switch (huaEnum) {
			case HAI_SITE_SERVICE:
				response = new HttpSiteService().execute(command);
				break;
			case HAI_USER_SERVICE:
				response = new HttpUserService().execute(command);
				break;
			case HAI_GROUP_SERVICE:
				response = new HttpGroupService().execute(command);
				break;
			case HAI_FRIEND_SERVICE:
				response = new HttpFriendService().execute(command);
				break;
			case HAI_MESSAGE_SERVICE:
				response = new HttpMessageService().execute(command);
				break;
			case HAI_PUSH_SERVICE:
				response = new HttpPushService().execute(command);
				break;
			case HAI_SESSION_SERVICE:
				response = new HttpSessionService().execute(command);
				break;
			default:
				logger.error("error http request command={}", command.toString());
				throw new Exception("http request with error url=" + command.getUri());
			}
		} catch (Exception e) {
			logger.error("http request service error.", e);
		}
		if (response == null) {
			response = new CommandResponse().setErrCode(ErrorCode2.ERROR2_HTTP_URL);
		}
		return response.setVersion(CommandConst.PROTOCOL_VERSION).setAction(CommandConst.ACTION_RES);
	}

}
