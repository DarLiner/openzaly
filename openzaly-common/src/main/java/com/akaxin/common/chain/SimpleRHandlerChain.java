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
package com.akaxin.common.chain;

/**
 * 永远只执行chain中最后一个handler
 * 
 * @author Sam
 * @since 2017-09.30
 * @param <T>
 */
public class SimpleRHandlerChain<T, R> extends AbstractHandlerChain<T, R> {

	@Override
	public R handle(T t) {

		for (IHandler<T, R> handler : getHandlers()) {
			return handler.handle(t);
		}
		return null;
	}

}
