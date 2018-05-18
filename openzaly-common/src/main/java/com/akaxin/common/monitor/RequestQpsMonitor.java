package com.akaxin.common.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.akaxin.common.logs.LogCreater;

public class RequestQpsMonitor extends ZalyMonitor {
	private static Map<String, ZalyCounter> data = new HashMap<String, ZalyCounter>();

	public static ZalyCounter API_SITE_REGISTER = new ZalyCounter(1);
	public static ZalyCounter API_SITE_LOGIN = new ZalyCounter(2);

	public static ZalyCounter IM_ONLINE = new ZalyCounter(5);
	public static ZalyCounter IM_CTS_MESSAGE = new ZalyCounter(3);
	public static ZalyCounter IM_SYNC_MESSAGE = new ZalyCounter(4);

	static {
		data.put("ASR", API_SITE_REGISTER);
		data.put("ASL", API_SITE_LOGIN);
		data.put("IO", IM_ONLINE);
		data.put("ICM", IM_CTS_MESSAGE);
		data.put("ISM", IM_SYNC_MESSAGE);
	}

	@Override
	public List<String> buidHeader() {
		List<String> headers = new ArrayList<String>();
		headers.add("ASR");
		headers.add("ASL");
		headers.add("IO");
		headers.add("ICM");
		headers.add("ISM");
		return headers;
	}

	@Override
	public void buildBody(Map<String, String> monitorData) {
		for (String header : getHeader()) {
			monitorData.put(header, data.get(header).getCountString());
		}
	}

	@Override
	public long getIntervalTime() {
		return 1000;
	}

	@Override
	public void clear() {
		for (String req : data.keySet()) {
			if (!"IO".equals(req)) {
				ZalyCounter c = data.get(req);
				c.clear();
			}
		}
	}

	@Override
	public Logger getMonitorLogger() {
		return LogCreater.createLogger("req-qps");
	}

}
