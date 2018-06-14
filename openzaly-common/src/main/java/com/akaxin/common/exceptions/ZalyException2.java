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
package com.akaxin.common.exceptions;

import com.akaxin.common.constant.IErrorCode;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-26 13:31:49
 */
public class ZalyException2 extends Throwable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IErrorCode errCode;

	public ZalyException2(IErrorCode errCode) {
		super(errCode.toString());
		this.errCode = errCode;
	}

	public IErrorCode getErrCode() {
		return this.errCode;
	}
}
