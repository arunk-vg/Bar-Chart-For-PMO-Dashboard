package com.tngtech.jira.plugins.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Utils {
	public static Map<Long, String> sortMapByValue(Map<Long, String> unsortedMap) {
		Map<Long, String> sortedMap = new LinkedHashMap<Long, String>();

		for (int i = 0; i < unsortedMap.size(); i++) {
			Long currentKey = null;
			String currentValue = null;

			for (Map.Entry<Long, String> entry : unsortedMap.entrySet()) {
				if ((currentKey == null || currentValue.compareTo(entry.getValue()) > 0)
						&& !sortedMap.containsKey(entry.getKey())) {
					currentKey = entry.getKey();
					currentValue = entry.getValue();
				}
			}
			sortedMap.put(currentKey, currentValue);
		}

		return sortedMap;
	}

	@SuppressWarnings("rawtypes")
	public static List<String> toStringList(Collection collection) {
		List<String> stringList = new ArrayList<String>(collection.size());
		for (Object member : collection) {
			stringList.add(member.toString());
		}
		return stringList;
	}

}
