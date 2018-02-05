package com.akaxin.common.test;

import java.io.UnsupportedEncodingException;

public class EncodeTest {
	public static void printByteLength(String s, String encodingName) {
		System.out.print("字节数：");
		try {
			System.out.print(s.getBytes(encodingName).length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(";编码：" + encodingName);
	}

	public static void main(String[] args) {
		String en = "A";
		String ch = "人";

		// 计算一个英文字母在各种编码下的字节数
		System.out.println("英文字母：" + en);
		EncodeTest.printByteLength(en, "GB2312");
		EncodeTest.printByteLength(en, "GBK");
		EncodeTest.printByteLength(en, "GB18030");
		EncodeTest.printByteLength(en, "ISO-8859-1");
		EncodeTest.printByteLength(en, "UTF-8");
		EncodeTest.printByteLength(en, "UTF-16");
		EncodeTest.printByteLength(en, "UTF-16BE");
		EncodeTest.printByteLength(en, "UTF-16LE");

		System.out.println();

		System.out.println("中文汉字：" + ch);
		EncodeTest.printByteLength(ch, "GB2312");
		EncodeTest.printByteLength(ch, "GBK");
		EncodeTest.printByteLength(ch, "GB18030");
		EncodeTest.printByteLength(ch, "ISO-8859-1");
		EncodeTest.printByteLength(ch, "UTF-8");
		EncodeTest.printByteLength(ch, "UTF-16");
		EncodeTest.printByteLength(ch, "UTF-16BE");
		EncodeTest.printByteLength(ch, "UTF-16LE");
	}
}
