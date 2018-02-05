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
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.site.business.api.IRequest;

/**
 * API处理业务逻辑
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.16
 *
 */
public class ApiRequestService implements IRequest {
	private static final Logger logger = LoggerFactory.getLogger(ApiRequestService.class);

	public CommandResponse process(Command command) {
		try {
			return ApiServiceFactory.getService(command.getService()).execute(command);
		} catch (Exception e) {
			logger.error("api request service error.", e);
		}
		return new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION).setAction(CommandConst.ACTION_RES)
				.setErrCode(ErrorCode.ERROR);
	}

}
