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
package com.akaxin.site.business.impl.hai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.plugin.HaiMessageProxyProto;
import com.akaxin.proto.site.ImCtsMessageProto.ImCtsMessageRequest;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.message.api.IMessageService;
import com.akaxin.site.message.service.ImMessageService;

/**
 * 扩展使用的消息服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-14 12:03:49
 */
// @Service("message")
public class HttpMessageService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpMessageService.class);

	private IMessageService imService = new ImMessageService();

	/**
	 * 
	 * @param command
	 * @return
	 */
	// @method("proxy")
	public CommandResponse proxy(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiMessageProxyProto.HaiMessageProxyRequest request = HaiMessageProxyProto.HaiMessageProxyRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			ImCtsMessageRequest proxyMessage = request.getProxyMsg();

			Command proxyCommand = new Command();
			proxyCommand.setSiteUserId(siteUserId);
			proxyCommand.setAction(RequestAction.IM_CTS_MESSAGE.getName());
			proxyCommand.setParams(proxyMessage.toByteArray());
			proxyCommand.setProxy(true);// 设置为代理请求

			boolean result = imService.execute(proxyCommand);
			if (result) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR2_MESSAGE_SEND_FAIL;
			}

			LogUtils.requestInfoLog(logger, proxyCommand, "result={} request={}", result, request.toBuilder());
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}
}
