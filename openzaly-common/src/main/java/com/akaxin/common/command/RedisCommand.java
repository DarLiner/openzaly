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
package com.akaxin.common.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.akaxin.common.resp.AbstractParameter;
import com.akaxin.common.resp.RedisBytesParameter;
import com.akaxin.common.resp.RedisDoubleParameter;
import com.akaxin.common.resp.RedisIntegerParameter;
import com.akaxin.common.resp.RedisStringParameter;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:27:39
 */
public class RedisCommand {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final byte[] CRLF = "\r\n".getBytes(UTF8);

	private final List<AbstractParameter> arguments = new ArrayList<AbstractParameter>();

	public RedisCommand() {

	}

	public RedisCommand add(int n) {
		arguments.add(RedisIntegerParameter.of(n));
		return this;
	}

	public RedisCommand add(String s) {
		arguments.add(RedisStringParameter.of(s));
		return this;
	}

	public RedisCommand add(long n) {
		arguments.add(RedisIntegerParameter.of(n));
		return this;
	}

	public RedisCommand add(double n) {
		arguments.add(RedisDoubleParameter.of(n));
		return this;
	}

	public RedisCommand add(byte[] value) {
		arguments.add(RedisBytesParameter.of(value));
		return this;
	}

	public RedisCommand addAll(List<AbstractParameter> paramList) {
		arguments.addAll(paramList);
		return this;
	}

	public void encode(ByteBuffer buf) {
		buf.put((byte) '*');
		RedisIntegerParameter.writeInteger(buf, arguments.size());
		buf.put(CRLF);
		for (AbstractParameter argument : arguments) {
			argument.encode(buf);
		}

	}

	public int getByteSize() {
		int byteSize = CRLF.length + 1;
		byteSize += RedisIntegerParameter.getIntegerByteSize(arguments.size());
		for (AbstractParameter argument : arguments) {
			byteSize += argument.getSize();
		}
		return byteSize + 10;
	}

	public String getParameterByIndex(int i) {
		if (i > arguments.size()) {
			return null;
		}
		return arguments.get(i).getValue();
	}

	public byte[] getBytesParamByIndex(int i) {
		if (i > arguments.size()) {
			return null;
		}
		return arguments.get(i).getBytesValue();
	}

}
