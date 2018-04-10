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
package com.akaxin.common.executor.chain.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:26:07
 * @param <T>
 */
public class MethodReflectHandler<T, R> implements IHandler<T, R> {
	private static final Logger logger = LoggerFactory.getLogger(MethodReflectHandler.class);

	@SuppressWarnings("unchecked")
	public R handle(T t) {
		try {
			Command cmd = (Command) t;
			String methodName = cmd.getMethod();

			Method m = this.getClass().getDeclaredMethod(methodName, cmd.getClass());
			Object result = m.invoke(this, t);

			if (result != null) {
				return (R) result;
			}
		} catch (NoSuchMethodException e) {
			logger.error("method handler NoSuchMethod error.", e);
		} catch (SecurityException e) {
			logger.error("method handler Security error.", e);
		} catch (IllegalAccessException e) {
			logger.error("method handler IllegalAccess error.", e);
		} catch (IllegalArgumentException e) {
			logger.error("method handler IllegalArgument error.", e);
		} catch (InvocationTargetException e) {
			logger.error("method handler InvocationTarget error.", e);
		}
		return null;
	}

}
