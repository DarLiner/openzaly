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
package com.akaxin.site.connector.handler;

import com.akaxin.common.chain.IHandler;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;

import io.netty.channel.Channel;

public abstract class AbstractCommonHandler<T, R> implements IHandler<T, R> {

	// default error ressponse by ErrorCode2.ERROR
	protected CommandResponse defaultErrorResponse() {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		commandResponse.setErrCode2(ErrorCode2.ERROR);
		return commandResponse;
	}

	// defined by user
	protected CommandResponse customResponse(ErrorCode2 errCode) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		commandResponse.setErrCode2(errCode);
		return commandResponse;
	}

	protected void closeChannel(Channel channel) {
		channel.close();
	}
}
