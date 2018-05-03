package com.akaxin.site.web.chat.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.business.utils.FilePathUtils;

@Controller
@RequestMapping("file")
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	// 跳转到聊天主页面
	@RequestMapping("/site-logo")
	// @ResponseBody
	public String getSiteLogo(HttpServletRequest request, HttpServletResponse response) {
		String logoFileId = SiteConfig.getSiteLogo();
		System.out.println("fileId=" + logoFileId);

		if (StringUtils.isNotEmpty(logoFileId)) {
			String fileUrl = FilePathUtils.getFilePathByFileId(logoFileId);
			System.out.println("fileUrl = " + fileUrl);
			File file = new File(fileUrl);

			if (file.exists()) {
				response.setContentType("application/force-download");
				response.addHeader("Content-Disposition", "attachment;fileName=" + logoFileId);
				// 每次写1M缓存数据大小
				byte[] bufferContent = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;

				try {
					// 通过response写出
					OutputStream ops = response.getOutputStream();
					// 获取图片文件的bufferBytes
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);

					// 开始循环读内容
					while (true) {
						int len = bis.read(bufferContent);
						if (len != -1) {
							ops.write(bufferContent, 0, len);
						} else {
							break;
						}
					}
					logger.info("download site logo success!");
					fis.close();
					bis.close();
				} catch (Exception e) {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e1) {
							logger.error("close FileInputStream error", e1);
						}
					}

					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e2) {
							logger.error("close BufferedInputStream error", e2);
						}
					}
				}
			} else {
				System.out.println("文件不存在");
			}
		}
		return null;
	}

}
