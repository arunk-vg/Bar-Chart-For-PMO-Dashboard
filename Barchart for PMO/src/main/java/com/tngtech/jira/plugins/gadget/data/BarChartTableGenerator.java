package com.tngtech.jira.plugins.gadget.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tngtech.jira.plugins.utils.JiraUtils;

public class BarChartTableGenerator {

	private final List<BarChartEntry> chartData;

	private List<String> foundXValues;
	private List<String> foundGroupValues;
	
	private final JiraUtils jiraUtils;

	// One line for each origin + the sum + the header row
	private final int rowcount;
	// One column for each month + the sum
	private final int columncount;

	public BarChartTableGenerator(JiraUtils jiraUtils, List<BarChartEntry> chartData) {
		this.jiraUtils = jiraUtils;
		this.chartData = chartData;
		findAllXValues();
		findAllGroupValues();
		rowcount = foundGroupValues.size() + 2;
		columncount = foundXValues.size() + 1;
	}

	private void findAllXValues() {
		foundXValues = new ArrayList<String>(chartData.size());
		for (BarChartEntry entry : chartData) {
			String xValue = entry.getXAxisValue();
			if (xValue != null && !xValue.isEmpty() && !foundXValues.contains(xValue)) {
				foundXValues.add(xValue);
			}
		}
	}

	public List<String> getFoundXValues() {
		return foundXValues;
	}

	private void findAllGroupValues() {
		foundGroupValues = new ArrayList<String>(chartData.size());
		for (BarChartEntry entry : chartData) {
			String groupValue = entry.getGroupFieldValue();
			if (groupValue != null && !groupValue.isEmpty() && !foundGroupValues.contains(groupValue)) {
				foundGroupValues.add(groupValue);
			}
		}
	}

	public List<String> getFoundGroupValues() {
		return foundGroupValues;
	}

	public List<List<String>> extractTableData() {
		List<List<String>> tableData = new ArrayList<List<String>>(rowcount);
		tableData.add(createHeaderRow());

		for (int i = 0; i < rowcount - 2; i++) {
			String thisRowsGroupValue = foundGroupValues.get(i);
			List<BarChartEntry> relevantEntries = getEntriesWithGroupValue(thisRowsGroupValue);
			Map<String, Integer> dataMap = splitEntriesOnXValues(relevantEntries);
			List<String> rowData = convertDataMapToRow(dataMap);
			tableData.add(rowData);
		}

		List<String> lastRowData = calculateSumsRow(tableData);
		tableData.add(lastRowData);
		return tableData;
	}

	private List<String> createHeaderRow() {
		List<String> headerRow = new ArrayList<String>(columncount);
		headerRow.addAll(foundXValues);
		headerRow.add(jiraUtils.getTranslatedText("gadget.barchart.view.table.sum"));
		return headerRow;
	}

	private List<BarChartEntry> getEntriesWithGroupValue(String groupValue) {
		List<BarChartEntry> relevantEntries = new ArrayList<BarChartEntry>(chartData.size());
		for (BarChartEntry entry : chartData) {
			if (entry.getGroupFieldValue().equals(groupValue)) {
				relevantEntries.add(entry);
			}
		}
		return relevantEntries;
	}

	private Map<String, Integer> splitEntriesOnXValues(List<BarChartEntry> relevantEntries) {
		Map<String, Integer> relevantDataMap = new HashMap<String, Integer>(columncount - 1);

		for (BarChartEntry entry : relevantEntries) {
			String thisXValue = entry.getXAxisValue();
			Integer newValue = Integer.valueOf(entry.getValue());
			if (relevantDataMap.containsKey(thisXValue)) {
				newValue += relevantDataMap.get(thisXValue);
			}
			relevantDataMap.put(thisXValue, newValue);
		}

		return relevantDataMap;
	}

	private List<String> convertDataMapToRow(Map<String, Integer> relevantEntries) {
		List<String> rowData = new ArrayList<String>(columncount);
		if (relevantEntries.size() == 0) {
			return rowData;
		}
		Integer valuesCounter = Integer.valueOf(0);

		for (String xValue : foundXValues) {
			if (relevantEntries.containsKey(xValue)) {
				Integer entryAmount = relevantEntries.get(xValue);
				rowData.add(entryAmount.toString());
				valuesCounter += entryAmount;
			} else {
				rowData.add("0");
			}
		}

		rowData.add(valuesCounter.toString());
		return rowData;
	}

	private List<String> calculateSumsRow(List<List<String>> tableData) {
		List<String> extractedSumsRow = new ArrayList<String>(columncount);
		for (int i = 0; i < columncount; i++) {
			Integer value = Integer.valueOf(0);
			for (int j = 1; j < rowcount - 1; j++) {
				value += Integer.valueOf(tableData.get(j).get(i));
			}
			extractedSumsRow.add(value.toString());
		}
		return extractedSumsRow;
	}

}
