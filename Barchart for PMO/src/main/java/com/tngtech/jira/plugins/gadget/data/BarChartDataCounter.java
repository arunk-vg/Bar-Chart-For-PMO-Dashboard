package com.tngtech.jira.plugins.gadget.data;

import java.util.List;

public class BarChartDataCounter {

	private List<BarChartEntry> chartData;

	public BarChartDataCounter(List<BarChartEntry> chartData) {
		this.chartData = chartData;
	}

	public int countEntries() {
		int count = 0;
		for (BarChartEntry entry : chartData) {
			if (entry.getGroupFieldValue() != null && !entry.getGroupFieldValue().isEmpty()) {
				count += entry.getValue();
			}
		}
		return count;
	}

}
