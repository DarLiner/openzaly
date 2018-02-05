package com.akaxin.site.business.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
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
		try {
			logger.info("AbstractApiBusiness command={}", command.toString());
			String methodName = command.getMethod();
			Method m = this.getClass().getDeclaredMethod(methodName, command.getClass());
			CommandResponse result = (CommandResponse) m.invoke(this, command);
			return result;
		} catch (NoSuchMethodException e) {
			logger.error("request business NoSuchMethod error.", e);
		} catch (SecurityException e) {
			logger.error("request business Security error.", e);
		} catch (IllegalAccessException e) {
			logger.error("request business IllegalAccess error.", e);
		} catch (IllegalArgumentException e) {
			logger.error("request business IllegalArgument error.", e);
		} catch (InvocationTargetException e) {
			logger.error("request business InvocationTarget error.", e);
		}
		return new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION).setAction(CommandConst.ACTION_RES)
				.setErrCode(ErrorCode.ERROR);
	}
}
