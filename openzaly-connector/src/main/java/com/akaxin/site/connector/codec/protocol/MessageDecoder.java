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
package com.akaxin.site.connector.codec.protocol;

import java.util.List;

import com.akaxin.site.connector.codec.parser.IProtocolParser;
import com.akaxin.site.connector.codec.parser.ProtocolParser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * 解码器,使用ReplayingDecoder，每一个deccode必须是
 * 
 * @author Sam
 * @since 2017.09.27
 *
 */
public class MessageDecoder extends ReplayingDecoder<ReplaySignal> {

	private IProtocolParser parser = new ProtocolParser();

	public MessageDecoder() {
		super.state(ReplaySignal.START_POINT);
	}

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		parser.readAndOut(ctx.channel(), in, out, this);

	}

	@Override
	public void checkpoint(ReplaySignal bp) {
		super.checkpoint(bp);
	}

	@Override
	public ReplaySignal state() {
		return super.state();
	}

}
