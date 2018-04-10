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

import com.akaxin.common.executor.chain.handler.IHandler;

/**
 * 定义一个简单统一的执行器
 * 
 * @author Sam
 * @since 2017.09.29
 *
 */
public class SimpleExecutor<T, R> extends AbstracteExecutor<T, R> {

	@Override
	public R execute(String name, T t) {
		IHandler<T, R> chain = getChain(name);
		return chain.handle(t);
	}

}
