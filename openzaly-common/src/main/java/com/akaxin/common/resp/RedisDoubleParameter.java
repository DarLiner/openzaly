package com.akaxin.common.resp;

import java.nio.ByteBuffer;

public class RedisDoubleParameter extends AbstractParameter {
	final double value;

	private RedisDoubleParameter(double value) {
		this.value = value;
	}

	public static RedisDoubleParameter of(double val) {
		return new RedisDoubleParameter(val);
	}

	@Override
	public void encode(ByteBuffer target) {
		RedisStringParameter.writeString(target, Double.toString(value));
	}

	@Override
	public String getValue() {
		return String.valueOf(value);
	}

	@Override
	public int getSize() {
		return RedisStringParameter.getStringByteSize(String.valueOf(value));
	}
}
