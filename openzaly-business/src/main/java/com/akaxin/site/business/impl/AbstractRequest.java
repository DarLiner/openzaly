package com.akaxin.site.business.impl;

import java.lang.reflect.Method;

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
		LogUtils.requestResultLog(logger, command, getResponseResult(response));
		return response;
	}

	private String getResponseResult(CommandResponse res) {
		return "errCode=" + res.getErrCode() + ",errInfo=" + res.getErrInfo();
	}
}
