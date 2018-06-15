package com.akaxin.common.utils;

import java.io.PrintWriter;

/**
 * 控制台输出信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-15 15:14:53
 */
public class PrintUtils {

	private static PrintWriter pw;

	public static PrintWriter getPW() {
		if (pw == null) {
			pw = new PrintWriter(System.out);
		}
		return pw;
	}

	public static void print(String messagePattern, Object... objects) {
		getPW().println(StringHelper.format(messagePattern, objects));
	}

	public static void print() {
		getPW().println();
	}

	public static void flush() {
		getPW().flush();
	}
}
