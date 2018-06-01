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
package com.akaxin.site.business.impl.hai;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CharsetCoding;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.crypto.AESCrypto;
import com.akaxin.common.exceptions.ZalyException2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.plugin.HaiSessionProfileProto;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.bean.SimpleAuthBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import com.akaxin.site.storage.service.UserSessionDaoService;

/**
 * <pre>
 * 	个人Session相关的扩展功能实现
 * 		hai/session/profile
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-01 12:07:32
 */
public class HttpSessionService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpSessionService.class);

	// session查询个人profile
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiSessionProfileProto.HaiSessionProfileRequest request = HaiSessionProfileProto.HaiSessionProfileRequest
					.parseFrom(command.getParams());
			String base64SessionId = request.getBase64SafeUrlSessionId();
			String authKey = command.getPluginAuthKey();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(base64SessionId, authKey)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			byte[] aesSessionId = Base64.getUrlDecoder().decode(base64SessionId);
			// 解密sessionId
			byte[] tsk = authKey.getBytes(CharsetCoding.ISO_8859_1);
			byte[] sessionIdBytes = AESCrypto.decrypt(tsk, aesSessionId);
			String sessionId = new String(sessionIdBytes, CharsetCoding.ISO_8859_1);

			// sessionid 查询 sessionID
			IUserSessionDao sessionDao = new UserSessionDaoService();
			SimpleAuthBean authBean = sessionDao.getUserSession(sessionId);

			if (authBean == null || StringUtils.isAnyEmpty(authBean.getSiteUserId(), authBean.getDeviceId())) {
				throw new ZalyException2(ErrorCode2.ERROR_SESSION);
			}

			String siteUserId = authBean.getSiteUserId();
			// 查询个人资料
			UserProfileBean bean = UserProfileDao.getInstance().getUserProfileById(siteUserId);
			if (bean == null || StringUtils.isEmpty(bean.getSiteUserId())) {
				throw new ZalyException2(ErrorCode2.ERROR2_USER_NOUSER);
			}

			UserProto.UserProfile profile = UserProto.UserProfile.newBuilder().setSiteUserId(bean.getSiteUserId())
					.setUserName(bean.getUserName()).setUserPhoto(String.valueOf(bean.getUserPhoto()))
					.setUserStatusValue(bean.getUserStatus()).build();
			HaiSessionProfileProto.HaiSessionProfileResponse response = HaiSessionProfileProto.HaiSessionProfileResponse
					.newBuilder().setUserProfile(profile).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

}
