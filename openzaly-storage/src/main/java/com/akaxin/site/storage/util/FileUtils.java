package com.akaxin.site.storage.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.akaxin.site.storage.dao.mysql.manager.MysqlManager;

public class FileUtils {

	public static void writeResourceToFile(String resourceName, File file) throws FileNotFoundException, IOException {
		if (!file.exists()) {
			new FileOutputStream(file).close();
		}
		InputStream is = MysqlManager.class.getResourceAsStream(resourceName);
		BufferedInputStream bis = new BufferedInputStream(is);
		FileOutputStream fos = new FileOutputStream(file);
		try {
			byte[] buffer = new byte[1024];
			int bytesLen = 0;
			while ((bytesLen = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesLen);
			}
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
}
