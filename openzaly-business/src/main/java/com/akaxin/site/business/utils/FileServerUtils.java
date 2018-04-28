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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.FileProto;
import com.akaxin.proto.core.FileProto.FileType;

/**
 * 文件服务相关操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:21:39
 */
public class FileServerUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileServerUtils.class);
	private static final String FILE_PREFFIX = "AKX-";

	// 获取当前时间 "20180428"
	private static String getDayTime() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日
		return year + "" + (month < 10 ? ("0" + month) : month) + "" + day;
	}

	// 生成存放文件的文件目录
	private static String createFileDir(String defaultDir, int type) {
		StringBuilder url = new StringBuilder(defaultDir);
		String filePath = SiteFileType.getFilePthByType(type);
		url.append(filePath).append("/");
		url.append(getDayTime());
		return url.toString();
	}

	// 生成文件名,老版本21位，新版本>21位
	private static String createFileName(FileProto.FileType type, FileProto.FileDesc fileDesc) {
		String fileName = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
		if (FileProto.FileType.MESSAGE_VOICE == type) {
			// 语音
			if (fileDesc != null && fileDesc.getLength() > 0) {
				fileName = fileName + "_" + fileDesc.getLength();
			}
		} else {
			// 图片
			if (fileDesc != null && fileDesc.getWidth() > 0 && fileDesc.getHeight() > 0) {
				fileName = fileName + "_" + fileDesc.getWidth() + "_" + fileDesc.getHeight();
			}
		}
		return fileName;
	}

	// 保存客户端上传的文件资源（图片，语音，视频等）
	public static String saveFile(byte[] imageBytes, String defaultDir, FileProto.FileType type,
			FileProto.FileDesc fileDesc) {
		String fileUrl = null;
		String fileName = null;
		BufferedOutputStream bos = null;
		try {
			if (!defaultDir.endsWith("/")) {
				defaultDir += "/";
			}
			// 文件目录
			String fileDir = createFileDir(defaultDir, type.getNumber());
			// 文件名称
			fileName = createFileName(type, fileDesc);

			// 存储的文件资源
			File storageFile = new File(fileDir, fileName);
			if (!storageFile.getParentFile().exists()) {
				storageFile.getParentFile().mkdirs();
			}

			if (!storageFile.exists()) {
				storageFile.createNewFile();
			}
			bos = new BufferedOutputStream(new FileOutputStream(storageFile));
			bos.write(imageBytes);

			fileUrl = storageFile.getPath();

			logger.debug("upload file URL={}", fileUrl);
		} catch (IOException e) {
			logger.error("uplaod file error.", e);
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				logger.error("close file output stream error.");
			}
		}

		// >21 新改版的fileId格式
		String fileId = fileUrl.substring(defaultDir.length(), fileUrl.length()).replaceAll("/", "-");
		if (fileName.length() != 21 || fileName.contains("_")) {
			return FILE_PREFFIX + fileId;
		}

		return fileId;
	}

	public static byte[] fileToBinary(String defaultDir, String fileUrl) {
		fileUrl = fileUrl.replaceAll("-", "/");
		if (defaultDir.endsWith("/")) {
			fileUrl = defaultDir + fileUrl;
		} else {
			fileUrl = defaultDir + "/" + fileUrl;
		}
		File file = new File(fileUrl);
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			byte[] bytes = new byte[bis.available()];
			bis.read(bytes);
			return bytes;
		} catch (IOException e) {
			logger.error("download file={} error,cause={}", fileUrl, e.toString());
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				logger.error("close file stream error.", e);
			}
		}
		return null;
	}

	public static byte[] fileToBinary(String fileUrl) {
		File file = new File(fileUrl);
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			byte[] bytes = new byte[bis.available()];
			bis.read(bytes);
			return bytes;
		} catch (IOException e) {
			logger.error("download file={} error,cause={}", fileUrl, e.toString());
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				logger.error("close file stream error.", e);
			}
		}
		return null;
	}

	enum SiteFileType {
		UNKNOW_FILE(FileType.UNKNOWN_FILE_VALUE, "UNKNOW"), // 用户，群头像
		USER_PORTRAIT(FileType.USER_PORTRAIT_VALUE, "U/PORT"), // 用户头像
		GROUP_PORTRAIT(FileType.GROUP_PORTRAIT_VALUE, "G/PORT"), // 群组头像

		// 需要增加文件描述
		MESSAGE_IMAGE(FileType.MESSAGE_IMAGE_VALUE, "MSG/IMG"), // 个人消息，群图片消息
		MESSAGE_VOICE(FileType.MESSAGE_VOICE_VALUE, "MSG/VOI"), // 个人，群语音消息

		SITE_PLUGIN(FileType.SITE_PLUGIN_VALUE, "SITE/PLUG"), // 站点扩展图片存放位置
		SITE_LOGO(FileType.SITE_ICON_VALUE, "SITE/LOGO");// 站点相关图片，如站点logo

		int type;
		String pth;

		SiteFileType(int type, String pth) {
			this.type = type;
			this.pth = pth;
		}

		public int getType() {
			return this.type;
		}

		public String getPth() {
			return this.pth;
		}

		public static String getFilePthByType(int type) {
			for (SiteFileType file : SiteFileType.values()) {
				if (type == file.getType()) {
					return file.getPth();
				}
			}
			return null;
		}
	}

}
