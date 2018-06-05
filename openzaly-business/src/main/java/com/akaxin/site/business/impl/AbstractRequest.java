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
package com.akaxin.site.business.impl;

import java.lang.reflect.Method;

import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;

/**
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:20:14
 */
public abstract class AbstractRequest implements IRequestService {
	private static final Logger logger = LoggerFactory.getLogger(AbstractRequest.class);

	public CommandResponse execute(Command command) {
		return executeMethodByReflect(command);
	}

	private CommandResponse executeMethodByReflect(Command command) {
		CommandResponse response = null;
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			String methodName = command.getMethod();
			Method m = this.getClass().getDeclaredMethod(methodName, command.getClass());
			response = (CommandResponse) m.invoke(this, command);
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, e);
		}

		if (response == null) {
			response = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
					.setAction(CommandConst.ACTION_RES).setErrCode2(errCode);
		}
		return response;
	}
    //检查请求的参数中的siteUserId是否是存在的
	public boolean checkUserId(String siteUserId) {
		UserProfileBean userProfile = UserProfileDao.getInstance().getUserProfileById(siteUserId);
		if (userProfile != null) {
			return true;
		}
		return false;
	}
    //检查请求的参数中的groupId是否是存在的
    public boolean checkGroupId(String groupId) {
        GroupProfileBean groupProfile = UserGroupDao.getInstance().getGroupProfile(groupId);
        if (groupProfile != null) {
            return true;
        }
        return false;
    }

}
