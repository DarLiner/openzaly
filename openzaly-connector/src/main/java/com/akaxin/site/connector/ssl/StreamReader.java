package com.akaxin.site.connector.ssl;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 */
public class StreamReader {
	private static final Logger logger = LoggerFactory.getLogger(StreamReader.class);

//	public String toByteArray(InputStream is) {
//		int i = 0;
//		StringBuilder buf = new StringBuilder();
//		try {
//			while ((i = is.read()) != -1) {
//				if (buf.length() > 0)
//					buf.append(",");
//				buf.append("(byte)");
//				buf.append(i);
//			}
//
//		} catch (Throwable e) {
//			logger.error("trun to byte array from inputStream error", e);
//		}
//
//		return buf.toString();
//	}

	public static void main(String[] args) {
		// StreamReader reader = new StreamReader();
		// System.out.println(reader.toByteArray(StreamReader.class.getResourceAsStream("/securesocket.jks")));

	}

}
