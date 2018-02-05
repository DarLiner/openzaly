package com.akaxin.common.resp;

import java.nio.ByteBuffer;

public class RedisStringParameter extends AbstractParameter {

	public final String value;

	public RedisStringParameter(String val) {
		this.value = val;
	}

	public static RedisStringParameter of(String val) {
		return new RedisStringParameter(val);
	}

	@Override
	public void encode(ByteBuffer target) {
		writeString(target, value);
	}

	public static void writeString(ByteBuffer target, String value) {
		target.put((byte) '$');
		byte[] valueByte = value.getBytes(UTF8);
		RedisIntegerParameter.writeInteger(target, valueByte.length);
		target.put(CRLF);
		target.put(valueByte);
		target.put(CRLF);
	}

	public static int getStringByteSize(String value) {
		int byteSize = CRLF.length * 2 + 1;
		byte[] valueByte = value.getBytes(UTF8);
		byteSize += RedisIntegerParameter.getIntegerByteSize(valueByte.length);
		byteSize += valueByte.length;
		return byteSize;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public int getSize() {
		return getStringByteSize(this.value);
	}

}
