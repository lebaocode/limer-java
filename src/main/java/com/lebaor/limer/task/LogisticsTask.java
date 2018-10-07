package com.lebaor.limer.task;

import com.lebaor.limer.data.Logistics;
import com.lebaor.utils.LogUtil;

public class LogisticsTask {
	public static void addTask(Logistics lt) {
		LogUtil.WEB_LOG.info(lt.toJSON());
	}
}
