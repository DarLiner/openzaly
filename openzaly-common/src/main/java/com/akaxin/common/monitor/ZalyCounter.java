package com.akaxin.common.monitor;

import java.util.concurrent.atomic.AtomicLong;

public class ZalyCounter {
	private final AtomicLong count;

	public ZalyCounter() {
		count = new AtomicLong(0);
	}

	public ZalyCounter(int num) {
		this.count = new AtomicLong(num);
	}

	public long inc() {
		return count.incrementAndGet();
	}

	public long inc(long num) {
		return count.addAndGet(num);
	}

	public long dec() {
		return count.decrementAndGet();
	}

	public long dec(long num) {
		return count.getAndAdd(-num);
	}

	public long getCount() {
		return count.get();
	}

	public String getCountString() {
		return String.valueOf(count.get());
	}

	public void clear() {
		count.set(0);
	}

}
