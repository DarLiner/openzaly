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
package com.akaxin.common.executor;

import java.util.HashMap;
import java.util.Map;

import com.akaxin.common.chain.IHandler;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:31:26
 * @param <T>
 */
public abstract class AbstracteExecutor<T,R> implements IExecutor<T,R> {

	protected Map<String, IHandler<T,R>> executors = new HashMap<String, IHandler<T,R>>();
	protected Map<String, IHandler<T,R>> regexExecutors = new HashMap<String, IHandler<T,R>>();

	public abstract R execute(String name, T t);

	public AbstracteExecutor<T,R> addChain(String name, IHandler<T,R> chain) {
		executors.put(name, chain);
		return this;
	}

	public AbstracteExecutor<T,R> addRegexChain(String regexName, IHandler<T,R> regexChain) {
		regexExecutors.put(regexName, regexChain);
		return this;
	}

	public IHandler<T,R> getChain(String name) {
		IHandler<T,R> handler = executors.get(name);
		return handler;
	}

}
