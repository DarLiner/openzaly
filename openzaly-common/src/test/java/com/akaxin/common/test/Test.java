package com.akaxin.common.test;

import java.io.UnsupportedEncodingException;

public class Test {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String hello1 = "好";
		
		for (byte ss : hello1.getBytes("GB2312")) {
			System.out.print(ss+" ");
		}
		System.out.println();
		for (byte ss : hello1.getBytes("UTF-8")) {
			System.out.print(ss+" ");
		}
	}
}