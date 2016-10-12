package com.tngtech.jira.plugins.gadget.data;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.google.common.annotations.VisibleForTesting;
import com.tngtech.jira.plugins.utils.JiraUtils;
import com.tngtech.jira.plugins.utils.fields.FieldValueFinder;

public class BarChartDataCollector {

	private final FieldValueFinder finder;
	private final BarChartDataSorter sorter;

	private boolean groupByNone = false;
	private String groupFieldName = "";
	private String xAxisFieldName = "";

	public BarChartDataCollector(JiraUtils jiraUtils) {
		this(new FieldValueFinder(jiraUtils));
	}

	@VisibleForTesting
	public BarChartDataCollector(FieldValueFinder finder) {
		this(finder, new BarChartDataSorter(finder));
	}

	@VisibleForTesting
	public BarChartDataCollector(FieldValueFinder finder, BarChartDataSorter sorter) {
		this.finder = finder;
		this.sorter = sorter;
	}

	public List<BarChartEntry> collectChartData(List<Issue> issues, String xAxisFieldIdString, String groupFieldIdString) {
		List<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		if (issues == null || issues.size() == 0) {
			return chartData;
		}

		// Resetting field names every time is faster then check for last issue
		for (Issue issue : issues) {
			List<String> xAxisFieldValues = finder.findXAxisFieldValues(issue, xAxisFieldIdString);
			xAxisFieldName = finder.getLastFieldName();
			//System.out.println("X axis Field Name : "+xAxisFieldName);
			List<String> groupFieldValues = finder.findGroupFieldValues(issue, groupFieldIdString);
			groupByNone = finder.getLastFieldWasNone();
			groupFieldName = finder.getLastFieldName();
			for (String xAxisFieldValue : xAxisFieldValues) {
				for (String groupFieldValue : groupFieldValues) {
					insertEntry(chartData, xAxisFieldValue, groupFieldValue);
				}
			}
		}

		return sorter.sortAndEnrichChartData(xAxisFieldIdString, groupFieldIdString, chartData);
	}

	private void insertEntry(List<BarChartEntry> chartData, String xAxisFieldValue, String groupFieldValue) {
		boolean done = false;
		for (BarChartEntry entry : chartData) {
			if (entry.getXAxisValue().equals(xAxisFieldValue) && entry.getGroupFieldValue().equals(groupFieldValue)) {
				entry.increaseValue();
				done = true;
				break;
			}
		}
		if (!done) {
			chartData.add(new BarChartEntry(xAxisFieldValue, groupFieldValue));
		}
	}

	public String getGroupFieldName() {
		return groupFieldName;
	}

	public boolean getGroupByFieldIsNone() {
		return groupByNone;
	}

	public String getXAxisFieldName() {
		return xAxisFieldName;
	}
}