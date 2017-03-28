package com.bankicp.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskSort {
	/**
	 * 去除重复任务
	 * @param li
	 * @return
	 */
	public static List<TaskInfo> getSingleTaskList(List<TaskInfo> li) {
		List<TaskInfo> list = new ArrayList<TaskInfo>();
		if (li == null || li.size() == 0)
			return list;
		sort(li);
		Map<String, TaskInfo> map = new HashMap<String, TaskInfo>();
		for (TaskInfo taskInfo : li) {
			map.put(taskInfo.getCabinet().getCabinetName(), taskInfo);
		}
		for (Map.Entry<String, TaskInfo> entry : map.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	/**
	 * 列表OrderNo排序
	 */
	private static void sort(List<TaskInfo> list) {
		ItemInfoComparator comparator = new ItemInfoComparator();
		Collections.sort(list, comparator);
	}
}
