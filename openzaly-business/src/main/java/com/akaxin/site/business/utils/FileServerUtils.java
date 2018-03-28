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

import com.akaxin.proto.core.FileProto.FileType;

/**
 * 文件服务相关操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:21:39
 */
public class FileServerUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileServerUtils.class);

	public static String saveFile(byte[] imageBytes, String defaultDir, int type) {
		String fileUrl = null;
		BufferedOutputStream bos = null;
		try {
			if (!defaultDir.endsWith("/")) {
				defaultDir += "/";
			}
			String parentDir = createParentDir(defaultDir, type);
			String fileSuffix = UUID.randomUUID().toString().substring(0, 8);
			String file = System.currentTimeMillis() + fileSuffix;
			File storageFile = new File(parentDir, file);
			if (!storageFile.getParentFile().exists()) {
				storageFile.getParentFile().mkdirs();
			}

			if (!storageFile.exists()) {
				storageFile.createNewFile();
			}
			bos = new BufferedOutputStream(new FileOutputStream(storageFile));
			bos.write(imageBytes);

			fileUrl = storageFile.getPath();

			logger.info("upload file URL={}", fileUrl);
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

		return fileUrl.substring(defaultDir.length(), fileUrl.length()).replaceAll("/", "-");
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

	private static String createParentDir(String defaultDir, int type) {
		StringBuilder url = new StringBuilder(defaultDir);
		ImageType imageType = ImageType.getImageByType(type);
		url.append(imageType.getPth()).append("/");
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日
		url.append(year).append("/").append(month).append("/").append(day);
		return url.toString();
	}

	enum ImageType {
		UNKNOW_FILE(FileType.UNKNOWN_FILE_VALUE, "UNKNOW"), // 用户，群头像
		USER_PORTRAIT(FileType.USER_PORTRAIT_VALUE, "U/PORT"), // 用户头像
		MESSAGE_IMAGE(FileType.MESSAGE_IMAGE_VALUE, "MSG/IMG"), // 个人消息，群图片消息
		MESSAGE_VOICE(FileType.MESSAGE_VOICE_VALUE, "MSG/VOI"), // 个人，群语音消息
		GROUP_PORTRAIT(FileType.GROUP_PORTRAIT_VALUE, "G/PORT"), // 用户头像
		SITE_PLUGIN(FileType.SITE_PLUGIN_VALUE, "SITE/PLUG"), // 站点扩展图片存放位置
		SITE_LOGO(FileType.SITE_ICON_VALUE, "SITE/LOGO");// 站点相关图片，如站点logo

		int type;
		String pth;

		ImageType(int type, String pth) {
			this.type = type;
			this.pth = pth;
		}

		public int getType() {
			return this.type;
		}

		public String getPth() {
			return this.pth;
		}

		public static ImageType getImageByType(int type) {
			for (ImageType image : ImageType.values()) {
				if (type == image.getType()) {
					return image;
				}
			}
			return null;
		}

		public static ImageType getImageByPth(String pth) {
			for (ImageType image : ImageType.values()) {
				if (image.getPth().equals(pth)) {
					return image;
				}
			}
			return null;
		}

	}

}
