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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.proto.core.UicProto;
import com.akaxin.proto.core.UicProto.UicStatus;
import com.akaxin.proto.plugin.HaiUicCreateProto;
import com.akaxin.proto.plugin.HaiUicInfoProto;
import com.akaxin.proto.plugin.HaiUicListProto;
import com.akaxin.site.business.dao.SiteUicDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.bean.UicBean;

/**
 * hai接口，扩展管理服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-10 18:59:10
 */
public class HttpUICService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpUICService.class);

	/**
	 * 生成UIC（用户邀请码）
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse create(Command command) {
		logger.info("/hai/uic/create");
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiUicCreateProto.HaiUicCreateRequest request = HaiUicCreateProto.HaiUicCreateRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			int num = request.getUicNumber();
			int successCount = 0;

			logger.info("/hai/uic/create command={},request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(siteUserId) && num > 0) {
				UicBean bean = new UicBean();
				bean.setStatus(UicStatus.UNUSED_VALUE);
				bean.setCreateTime(System.currentTimeMillis());
				if (SiteUicDao.getInstance().batchAddUic(bean, num)) {
					errorCode = ErrorCode2.SUCCESS;
				}
			} else {
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}
			logger.info("create uic siteUserId={},succNum={},totalNumber={}", siteUserId, successCount, num);
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("add plugin error.", e);
		}
		logger.info("/hai/uic/create result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 分页获取UIC
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse list(Command command) {
		logger.info("/hai/uic/list");
		CommandResponse commandResponse = new CommandResponse();
		String errorCode = ErrorCode.ERROR;
		try {
			HaiUicListProto.HaiUicListRequest request = HaiUicListProto.HaiUicListRequest
					.parseFrom(command.getParams());
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			int status = request.getStatusValue();
			logger.info("/hai/uic/list command={},request={}", command.toString(), request.toString());

			List<UicBean> uicList = SiteUicDao.getInstance().getUicList(pageNum, pageSize, status);
			if (uicList != null) {
				HaiUicListProto.HaiUicListResponse.Builder responseBuilder = HaiUicListProto.HaiUicListResponse
						.newBuilder();
				for (UicBean bean : uicList) {
					responseBuilder.addUicInfo(getUicInfo(bean));
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errorCode = ErrorCode.SUCCESS;
			}

		} catch (Exception e) {
			commandResponse.setErrInfo("uic list error");
			logger.error("uic list error.", e);
		}
		return commandResponse.setErrCode(errorCode);
	}

	/**
	 * 获取UIC信息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse info(Command command) {
		logger.info("/hai/uic/info");
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiUicInfoProto.HaiUicInfoRequest request = HaiUicInfoProto.HaiUicInfoRequest
					.parseFrom(command.getParams());
			int id = request.getId();
			String uic = request.getUic();
			logger.info("/hai/uic/info request={}", request.toString());

			if (id > 0 && StringUtils.isNotBlank(uic)) {
				UicBean bean = SiteUicDao.getInstance().getUicInfo(uic);
				if (bean != null) {
					HaiUicInfoProto.HaiUicInfoResponse response = HaiUicInfoProto.HaiUicInfoResponse.newBuilder()
							.setUicInfo(getUicInfo(bean)).build();
					commandResponse.setParams(response.toByteArray());
					errorCode = ErrorCode2.SUCCESS;
				}
			} else {
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("hai uic info error.", e);
		}
		logger.info("/hai/uic/info result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	private UicProto.UicInfo getUicInfo(UicBean bean) {
		UicProto.UicInfo.Builder uicBuilder = UicProto.UicInfo.newBuilder();
		uicBuilder.setId(bean.getId());
		if (StringUtils.isNotBlank(bean.getSiteUserId())) {
			uicBuilder.setSiteUserId(bean.getSiteUserId());
		}
		if (StringUtils.isNotBlank(bean.getUic())) {
			uicBuilder.setUic(bean.getUic());
		}
		if (StringUtils.isNotBlank(bean.getUserName())) {
			uicBuilder.setUserName(bean.getUserName());
		}
		uicBuilder.setStatusValue(bean.getStatus());
		uicBuilder.setCreateTime(bean.getCreateTime());
		if (StringUtils.isNotBlank(bean.getUserName())) {
			uicBuilder.setUserName(bean.getUserName());
		}
		return uicBuilder.build();
	}

}