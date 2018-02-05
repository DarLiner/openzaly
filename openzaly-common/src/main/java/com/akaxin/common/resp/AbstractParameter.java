package com.akaxin.common.resp;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class AbstractParameter {
	protected static final Charset UTF8 = Charset.forName("UTF-8");
	protected static final byte[] CRLF = "\r\n".getBytes(UTF8);

	public abstract void encode(ByteBuffer buffer);

	public abstract int getSize();

	public String getValue() {
		return null;
	}

	public byte[] getBytesValue() {
		return null;
	}
}
