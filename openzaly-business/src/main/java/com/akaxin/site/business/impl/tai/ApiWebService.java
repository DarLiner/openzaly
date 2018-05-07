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
package com.akaxin.site.business.impl.tai;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.exceptions.ZalyException;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.site.ApiWebAuthProto;
import com.akaxin.site.business.cache.WebSessionCache;
import com.akaxin.site.business.impl.AbstractRequest;

/**
 * webim服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.25 15:10:36
 */
public class ApiWebService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(ApiWebService.class);

	public CommandResponse auth(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiWebAuthProto.ApiWebAuthRequest request = ApiWebAuthProto.ApiWebAuthRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String webSessionId = request.getSessionId();

			if (StringUtils.isAnyEmpty(webSessionId, siteUserId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			WebSessionCache.putWebAuthSession(webSessionId, siteUserId);
			errCode = ErrorCode2.SUCCESS;
		} catch (ZalyException e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

}
