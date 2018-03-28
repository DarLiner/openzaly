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
 * 简单的来说，只有返回true，此条消息链才算处理成功，如果返回false，则消息链终止执行，返回false
 * 
 * @author Sam
 * @since 2017-09.30
 * @param <T>
 */
public class SimpleHandlerChain<T> extends AbstractHandlerChain<T, Boolean> {

	@Override
	public Boolean handle(T t) {
		
		for (IHandler<T, Boolean> handler : getHandlers()) {
			if (!handler.handle(t)) {
				return false;
			}
		}
		return true;
	}

}
