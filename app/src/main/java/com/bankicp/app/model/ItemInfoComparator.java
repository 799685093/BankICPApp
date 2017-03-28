package com.bankicp.app.model;

import java.util.Comparator;
import java.util.Date;

import com.bankicp.app.utils.Util;

public class ItemInfoComparator implements Comparator<TaskInfo> {

	/**
	 * 如果o1小于o2,返回一个负数;如果o1大于o2，返回一个正数;如果他们相等，则返回0;
	 */
	@Override
	public int compare(TaskInfo o1, TaskInfo o2) {

		try {
			Date acceptTime1 = Util.toDate(o1.getTaskEndTime());
			Date acceptTime2 = Util.toDate(o2.getTaskEndTime());
			// 对日期字段进行升序，如果欲降序可采用before方法
			if (o1.getCabinet() == null || o2.getCabinet() == null) {
				return 0;
			}
			if (o1.getCabinet().getInspectionOrder() < o2.getCabinet()
					.getInspectionOrder())
				return 1;
			else if (o1.getCabinet().getInspectionOrder() > o2.getCabinet()
					.getInspectionOrder()) {
				return -1;
			} else {
				if (acceptTime1 != null && acceptTime2 != null
						&& acceptTime1.after(acceptTime2)) {
					return 1;
				} else {
					return -1;
				}

			}
		} catch (Exception e) {
		}
		return 0;
	}
}