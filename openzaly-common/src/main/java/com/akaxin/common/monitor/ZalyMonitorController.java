package com.akaxin.common.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.akaxin.common.threads.ThreadHelper;
import com.akaxin.common.utils.StringHelper;

public class ZalyMonitorController {
	private static Logger logger = Logger.getLogger(ZalyMonitorController.class);

	private List<ZalyMonitor> monitors = new ArrayList<ZalyMonitor>();

	public ZalyMonitorController() {
	}

	public ZalyMonitorController addMonitor(ZalyMonitor mon) {
		monitors.add(mon);
		return this;
	}

	public void start() {
		// 定时构建内容
		for (final ZalyMonitor monitor : monitors) {
			ThreadHelper.execute(new Runnable() {
				public void run() {
					while (true) {
						try {
							Map<String, String> monitorData = monitor.getBody();
							monitor.output(monitors, monitorData);
							monitor.clear();
							long interval = Math.max(monitor.getIntervalTime(), ZalyMonitor.INTERVAL_TIME);
							ThreadHelper.sleep(interval);
						} catch (Exception e) {
							logger.error(StringHelper.format("Monitor data error! monitor: {}",
									monitor.getClass().getName()), e);
						}
					}
				}
			});
		}

	}

}
