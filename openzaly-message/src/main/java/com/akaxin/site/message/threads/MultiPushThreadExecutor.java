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
package com.akaxin.site.message.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多线程push发送处理器
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-27 20:34:25
 */
public class MultiPushThreadExecutor {
	/**
	 * <pre>
	 * int corePoolSize = 5
	 * int maximumPoolSize=(n+1)*5 
	 * long keepAliveTime=10 
	 * TimeUnit unit=S
	 * BlockingQueue<Runnable> workQueue
	 * </pre>
	 */
	private static int maxThreadNum = (Runtime.getRuntime().availableProcessors() + 1) * 5;
	private static Executor threadPoolExecutor = new ThreadPoolExecutor(5, maxThreadNum, 10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

	private MultiPushThreadExecutor() {
	}

	public static Executor getExecutor() {
		return threadPoolExecutor;
	}
}
