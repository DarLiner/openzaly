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
package com.akaxin.site.business.utils;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.proto.core.ConfigProto.ConfigKey;
import com.akaxin.site.business.impl.site.SiteConfig;

/**
 * 提供站点服务文件路径位置，路径存放在站点配置表中。<br>
 * 第一次使用通过数据库查询获取，以后每次通过缓存中获取
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-14 14:59:38
 */
public class FilePathUtils {
	private static final String DEFAULT_FILE_PATH = "site-file/";
	private static String filePath;

	/**
	 * 
	 * @return
	 */
	public static String getPicPath() {
		if (filePath == null) {
			filePath = SiteConfig.getConfig(ConfigKey.PIC_PATH_VALUE);
		}
		if (StringUtils.isNotBlank(filePath)) {
			if (filePath.endsWith("/")) {
				return filePath + DEFAULT_FILE_PATH;
			}
			return filePath + "/" + DEFAULT_FILE_PATH;
		}
		return DEFAULT_FILE_PATH;
	}

	public static String getPicPath(String filePath) {
		if (StringUtils.isNotBlank(filePath)) {
			if (filePath.endsWith("/")) {
				return filePath + DEFAULT_FILE_PATH;
			}
			return filePath + "/" + DEFAULT_FILE_PATH;
		}
		return DEFAULT_FILE_PATH;
	}

	/**
	 * 通过fileId获取文件路径
	 * 
	 * @param fileId
	 * @return
	 */
	public static String getFilePathByFileId(String fileId) {
		// 获取文件目录
		String defaultDir = getPicPath();
		String fileUrl = fileId.replaceAll("-", "/");
		if (defaultDir.endsWith("/")) {
			fileUrl = defaultDir + fileUrl;
		} else {
			fileUrl = defaultDir + "/" + fileUrl;
		}
		return fileUrl;
	}

}
