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
package com.akaxin.site.message.executor;

import com.akaxin.common.chain.AbstractHandlerChain;
import com.akaxin.common.chain.SimpleHandlerChain;
import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.site.message.group.handler.GroupDetectionHandler;
import com.akaxin.site.message.group.handler.GroupMessageImageHandler;
import com.akaxin.site.message.group.handler.GroupMessageTextHandler;
import com.akaxin.site.message.group.handler.GroupMessageVoiceHandler;
import com.akaxin.site.message.group.handler.GroupMsgNoticeHandler;
import com.akaxin.site.message.group.handler.GroupPsnHandler;
import com.akaxin.site.message.group.handler.GroupPushHandler;
import com.akaxin.site.message.sync.handler.SyncFinishHandler;
import com.akaxin.site.message.sync.handler.SyncGroupMessageHandler;
import com.akaxin.site.message.sync.handler.SyncU2MessageHandler;
import com.akaxin.site.message.user2.handler.U2MessageImageHandler;
import com.akaxin.site.message.user2.handler.U2MessageImageSecretHandler;
import com.akaxin.site.message.user2.handler.U2MessageTextHandler;
import com.akaxin.site.message.user2.handler.U2MessageTextSecretHandler;
import com.akaxin.site.message.user2.handler.U2MessageVoiceHandler;
import com.akaxin.site.message.user2.handler.U2MessageVoiceSecretHandler;
import com.akaxin.site.message.user2.handler.U2MsgNoticeHandler;
import com.akaxin.site.message.user2.handler.UserDetectionHandler;
import com.akaxin.site.message.user2.handler.UserPsnHandler;
import com.akaxin.site.message.user2.handler.UserPushHandler;

public class MessageExecutor {
	private static AbstracteExecutor<Command> executor = new SimpleExecutor<Command>();

	static {
		AbstractHandlerChain<Command> u2MessageChain = new SimpleHandlerChain<Command>();
		u2MessageChain.addHandler(new UserDetectionHandler());
		u2MessageChain.addHandler(new U2MessageTextHandler());
		u2MessageChain.addHandler(new U2MessageTextSecretHandler());
		u2MessageChain.addHandler(new U2MessageImageHandler());
		u2MessageChain.addHandler(new U2MessageImageSecretHandler());
		u2MessageChain.addHandler(new U2MessageVoiceHandler());
		u2MessageChain.addHandler(new U2MessageVoiceSecretHandler());
		u2MessageChain.addHandler(new U2MsgNoticeHandler());
		u2MessageChain.addHandler(new UserPsnHandler());
		u2MessageChain.addHandler(new UserPushHandler());

		AbstractHandlerChain<Command> groupMessageChain = new SimpleHandlerChain<Command>();
		groupMessageChain.addHandler(new GroupDetectionHandler());
		groupMessageChain.addHandler(new GroupMessageTextHandler());
		groupMessageChain.addHandler(new GroupMessageImageHandler());
		groupMessageChain.addHandler(new GroupMessageVoiceHandler());
		groupMessageChain.addHandler(new GroupMsgNoticeHandler());
		groupMessageChain.addHandler(new GroupPsnHandler());
		groupMessageChain.addHandler(new GroupPushHandler());

		AbstractHandlerChain<Command> syncMessageChain = new SimpleHandlerChain<Command>();
		syncMessageChain.addHandler(new SyncU2MessageHandler());
		syncMessageChain.addHandler(new SyncGroupMessageHandler());

		executor.addChain("im.cts.message.u2", u2MessageChain);
		executor.addChain("im.cts.message.group", groupMessageChain);
		executor.addChain("im.sync.message", syncMessageChain);
		executor.addChain("im.sync.finish", new SyncFinishHandler());
	}

	public static AbstracteExecutor<Command> getExecutor() {
		return executor;
	}

}