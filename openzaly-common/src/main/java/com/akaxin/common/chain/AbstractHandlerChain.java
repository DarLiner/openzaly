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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:24:59
 */
public abstract class AbstractHandlerChain<T, R> implements IHandler<T, R> {

	private List<IHandler<T, R>> handlers = new ArrayList<IHandler<T, R>>();

	public abstract R handle(T t);

	public boolean addHandler(IHandler<T, R> handler) {
		return handlers.add(handler);
	}

	public List<IHandler<T, R>> getHandlers() {
		return handlers;
	}
}
