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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 使用TCP处理API请求,TCP代处理HTTP请求
 * 
 * @author Sam
 * @since 2017.10.19
 *
 * @param <Command>
 */
public class WSRequestHandler extends AbstractCommonHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(WSRequestHandler.class);

	public CommandResponse handle(Command command) {
		try {
			System.out.println(command.toString());
			System.out.println(command.getClientIp() + "," + command.getStartTime());
		} catch (Exception e) {
			logger.error("process ws request handler error", e);
		}
		return customResponse(ErrorCode2.ERROR);
	}

	private void tellClientSessionError(final Channel channel) {
		String action = CommandConst.ACTION_RES;
		channel.writeAndFlush("").addListener(new GenericFutureListener<Future<? super Void>>() {

			public void operationComplete(Future<? super Void> future) throws Exception {
				channel.close();
			}
		});
	}

}
