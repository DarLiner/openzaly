package com.akaxin.common.resp;

import java.nio.ByteBuffer;

public class RedisBytesParameter extends AbstractParameter {

	final byte[] value;

	public RedisBytesParameter(byte[] value) {
		this.value = value;
	}

	public static RedisBytesParameter of(byte[] value) {
		return new RedisBytesParameter(value);
	}

	@Override
	public void encode(ByteBuffer buffer) {
		writeBytes(buffer, value);
	}

	public static void writeBytes(ByteBuffer buffer, byte[] value) {

		buffer.put((byte) '$');

		RedisIntegerParameter.writeInteger(buffer, value.length);
		buffer.put(CRLF);

		buffer.put(value);
		buffer.put(CRLF);
	}

	public static int getByteSize(byte[] value) {
		int byteSize = CRLF.length * 2 + 1;
		byteSize += RedisIntegerParameter.getIntegerByteSize(value.length);
		byteSize += value.length;
		return byteSize;
	}

	@Override
	public String getValue() {
		return new String(value);
	}

	@Override
	public byte[] getBytesValue() {
		return this.value;
	}

	@Override
	public int getSize() {
		return getByteSize(this.value);
	}
}
