package com.akaxin.site.boot.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.FileProto.FileType;
import com.akaxin.site.business.utils.FilePathUtils;
import com.akaxin.site.business.utils.FileServerUtils;

public class TestFile {
	private static final Logger logger = LoggerFactory.getLogger(TestFile.class);

	private static String getDefaultSiteAdminIcon() {
		try {
			byte[] iconBytes = FileServerUtils.fileToBinary("Test.jpg");
			String fileId = FileServerUtils.saveFile(iconBytes, FilePathUtils.getPicPath(), FileType.GROUP_PORTRAIT,
					null);
			return fileId;
		} catch (Exception e) {
			logger.error("get default icon error", e);
		}
		return "";
	}

	public static void main(String args[]) {
		System.out.println(getDefaultSiteAdminIcon());
	}
}
