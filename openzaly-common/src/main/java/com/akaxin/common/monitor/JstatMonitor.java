package com.akaxin.common.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.akaxin.common.logs.LogCreater;
import com.akaxin.common.logs.LogUtils;

public class JstatMonitor extends ZalyMonitor {
	private static final Logger logger = Logger.getLogger(JstatMonitor.class);

	private List<String> statTitles = Arrays.asList("S0", "S1", "E", "O", "M", "CCS", "YGC", "YGCT", "FGC", "FGCT",
			"GCT");
	private Process process = null;
	private BufferedReader bufferedReader = null;

	public JstatMonitor() {
		try {
			String name = ManagementFactory.getRuntimeMXBean().getName();
			String pid = name.substring(0, name.indexOf("@"));
			process = Runtime.getRuntime().exec("jstat -gcutil " + pid + " " + getIntervalTime());

			LogUtils.info(logger, "Start jstat for java application pid={}", pid);

			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			bufferedReader = new BufferedReader(isr);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					close();
				}
			});

		} catch (Exception e) {
			logger.error("start monitor for jstat error.", e);
			close();
		}

	}

	@Override
	public synchronized void buildBody(Map<String, String> monitorData) {
		try {
			String line = null;
			if ((line = bufferedReader.readLine()) != null) {
				String[] stats = line.trim().split("[ ]+");
				if (stats.length == statTitles.size()) {
					for (int i = 0; i < statTitles.size(); i++) {
						monitorData.put(statTitles.get(i), stats[i]);
					}
				}
			}
		} catch (Exception e) {
			logger.error("build body for jstat monitor error", e);
		}
	}

	@Override
	public List<String> buidHeader() {
		return statTitles;
	}

	@Override
	public long getIntervalTime() {
		return 1000;
	}

	/**
	 * 清理所有监控数据和进程
	 */
	private void close() {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception e1) {
			logger.error("close bufferedReader error");
		}

		try {
			if (process != null) {
				process.destroy();
			}
		} catch (Exception e) {
			logger.error("destroy process error.", e);
		}

	}

	@Override
	public Logger getMonitorLogger() {
		return LogCreater.createLogger("jstat");
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
}
