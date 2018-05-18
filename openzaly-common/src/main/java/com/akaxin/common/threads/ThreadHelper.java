package com.akaxin.common.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadHelper {
	private static Logger logger = LoggerFactory.getLogger(ThreadHelper.class);

	private static ExecutorService threadPool = Executors.newCachedThreadPool();

	public static void execute(Runnable r) {
		threadPool.execute(r);
	}

	public static void sleep(long timeMillis) {
		try {
			Thread.sleep(timeMillis);
		} catch (Exception e) {
			logger.error("thread sleep error");
		}
	}
}
