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
package com.akaxin.site.connector.netty;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PrefixThreadFactory implements ThreadFactory {

	private final ThreadGroup threadGroup;
	private final String prefix;
	private final boolean isDaemon;
	private final AtomicInteger sequence = new AtomicInteger(0);

	public PrefixThreadFactory(String namePrefix) {
		this(namePrefix, false);
	}

	public PrefixThreadFactory(String threadNamePrefix, boolean isDaemon) {
		SecurityManager securityManager = System.getSecurityManager();
		this.threadGroup = (securityManager == null) ? Thread.currentThread().getThreadGroup()
				: securityManager.getThreadGroup();
		this.prefix = threadNamePrefix + "-thread-";
		this.isDaemon = isDaemon;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public Thread newThread(Runnable r) {
		final String name = prefix + sequence.getAndIncrement();
		Thread thread = new Thread(threadGroup, r, name, 0);
		thread.setDaemon(isDaemon);
		return thread;
	}

}
