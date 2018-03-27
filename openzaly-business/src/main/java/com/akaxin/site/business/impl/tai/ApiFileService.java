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
package com.akaxin.site.business.impl.tai;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.FileProto;
import com.akaxin.proto.site.ApiFileDownloadProto;
import com.akaxin.proto.site.ApiFileUploadProto;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.utils.FilePathUtils;
import com.akaxin.site.business.utils.FileServerUtils;
import com.google.protobuf.ByteString;

/**
 * 文件（图片，语音）上传下载
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-25 17:52:08
 */
public class ApiFileService extends AbstractRequest {
	private static Logger logger = LoggerFactory.getLogger(ApiFileService.class);

	public CommandResponse upload(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiFileUploadProto.ApiFileUploadRequest request = ApiFileUploadProto.ApiFileUploadRequest
					.parseFrom(command.getParams());
			FileProto.File file = request.getFile();
			int type = file.getFileTypeValue();
			byte[] content = file.getFileContent().toByteArray();
			LogUtils.apiRequestLog(logger, command, "type=" + type + ",size=" + content.length);

			String fileId = FileServerUtils.saveFile(content, FilePathUtils.getPicPath(), type);
			ApiFileUploadProto.ApiFileUploadResponse response = ApiFileUploadProto.ApiFileUploadResponse.newBuilder()
					.setFileId(fileId).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.apiErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	public CommandResponse download(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiFileDownloadProto.ApiFileDownloadRequest request = ApiFileDownloadProto.ApiFileDownloadRequest
					.parseFrom(command.getParams());
			String fileId = request.getFileId();
			LogUtils.apiRequestLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(fileId) && !"null".equals(fileId)) {
				byte[] imageBytes = FileServerUtils.fileToBinary(FilePathUtils.getPicPath(), fileId);

				FileProto.File file = FileProto.File.newBuilder().setFileId(fileId)
						.setFileContent(ByteString.copyFrom(imageBytes)).build();

				ApiFileDownloadProto.ApiFileDownloadResponse response = ApiFileDownloadProto.ApiFileDownloadResponse
						.newBuilder().setFile(file).build();

				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.apiErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

}
