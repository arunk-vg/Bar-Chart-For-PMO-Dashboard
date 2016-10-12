package com.tngtech.jira.plugins.gadget.data;

import java.util.ArrayList;
import java.util.List;

import com.tngtech.jira.plugins.utils.fields.FieldValueFinder;

public class BarChartDataSorter {

	private final FieldValueFinder finder;

	private List<String> possibleXAxisValues = null;
	private List<String> sortedGroupValues = null;

	public BarChartDataSorter(FieldValueFinder finder) {
		this.finder = finder;
	}

	public List<BarChartEntry> sortAndEnrichChartData(String xAxisFieldIdString, String groupFieldIdString,
			List<BarChartEntry> chartData) {
		possibleXAxisValues = finder.getPossibleXAxisValues(xAxisFieldIdString);
		if (!possibleXAxisValues.equals(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST)) {
			possibleXAxisValues.addAll(getNeededDefaultValuesForXAxis(chartData));
		}
		sortedGroupValues = getNeededGroupValues(groupFieldIdString, chartData);
		List<BarChartEntry> enrichedChartData = prependFakeEntries(chartData);
		List<BarChartEntry> sortedChartData = sortEntriesAndFillGaps(enrichedChartData);

		addFakeEntriesForMissingAtEnd(sortedChartData);
		improveFakeBarChartEntries(sortedChartData);
		return sortedChartData;
	}

	private List<String> getNeededDefaultValuesForXAxis(List<BarChartEntry> chartData) {
		List<String> defaultValues = new ArrayList<String>();
		for (BarChartEntry entry : chartData) {
			String xAxisValue = entry.getXAxisValue();
			if (FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST.contains(xAxisValue) && !defaultValues.contains(xAxisValue)) {
				defaultValues.add(xAxisValue);
			}
		}
		return defaultValues;
	}

	private List<String> getNeededGroupValues(String groupFieldIdString, List<BarChartEntry> chartData) {
		List<String> groupValues = new ArrayList<String>();
		groupValues.addAll(finder.getPossibleGroupFieldValues(groupFieldIdString));

		for (int i = 0; i < chartData.size(); i++) {
			BarChartEntry buffer = null;
			for (BarChartEntry entry : chartData) {
				if ((buffer == null || entry.getGroupFieldValue().compareTo(buffer.getGroupFieldValue()) < 0)
						&& !entry.getGroupFieldValue().equals(FieldValueFinder.DEFAULT_FIELD_VALUE)
						&& !groupValues.contains(entry.getGroupFieldValue())) {
					buffer = entry;
				}
			}
			if (buffer == null) {
				break;
			}
		}

		return groupValues;
	}

	private List<BarChartEntry> prependFakeEntries(List<BarChartEntry> chartData) {
		List<BarChartEntry> enrichedChartData = new ArrayList<BarChartEntry>();
		for (String groupValue : sortedGroupValues) {
			enrichedChartData.add(new FakeBarChartEntry(getFirstXValue(chartData), groupValue));
		}
		enrichedChartData.addAll(chartData);
		return enrichedChartData;
	}

	private String getFirstXValue(List<BarChartEntry> chartData) {
		if (!possibleXAxisValues.get(0).equals(FieldValueFinder.DEFAULT_FIELD_VALUE)) {
			return possibleXAxisValues.get(0);
		} else {
			BarChartEntry buffer = null;
			for (BarChartEntry entry : chartData) {
				if ((buffer == null || entry.getXAxisValue().compareTo(buffer.getXAxisValue()) < 0)
						&& (!entry.getXAxisValue().equals(FieldValueFinder.DEFAULT_FIELD_VALUE) || chartData.size() <= 1)) {
					buffer = entry;
				}
			}
			return buffer.getXAxisValue();
		}
	}

	private List<BarChartEntry> sortEntriesAndFillGaps(List<BarChartEntry> chartData) {
		List<BarChartEntry> sortedChartData = new ArrayList<BarChartEntry>();
		int xAxisValuesCounter = 0;
		BarChartEntry last = null;

		for (int i = 0; i < chartData.size(); i++) {
			BarChartEntry buffer = null;
			for (BarChartEntry current : chartData) {
				if (currentIsPriorToBuffer(sortedChartData, buffer, current)) {
					buffer = current;
				}
			}

			while (xAxisValuesCounter < possibleXAxisValues.size()
					&& xAxisValuesCounter < possibleXAxisValues.indexOf(buffer.getXAxisValue())) {
				sortedChartData.add(new FakeBarChartEntry(possibleXAxisValues.get(xAxisValuesCounter)));
				xAxisValuesCounter++;
			}

			sortedChartData.add(buffer);
			if (last == null || !last.getXAxisValue().equals(buffer.getXAxisValue())) {
				xAxisValuesCounter++;
			}
			last = buffer;
		}

		return sortedChartData;
	}

	private boolean currentIsPriorToBuffer(List<BarChartEntry> sortedChartData, BarChartEntry buffer,
			BarChartEntry current) {
		return (buffer == null || hasLowerXValue(buffer, current)) && !sortedChartData.contains(current);
	}

	private boolean hasLowerXValue(BarChartEntry buffer, BarChartEntry current) {
		int bufferIndex = possibleXAxisValues.indexOf(buffer.getXAxisValue());
		int currentIndex = possibleXAxisValues.indexOf(current.getXAxisValue());
		if (bufferIndex != -1 && currentIndex != -1) {
			return currentIndex < bufferIndex;
		} else {
			return buffer.getXAxisValue().compareTo(current.getXAxisValue()) > 0;
		}
	}

	private void addFakeEntriesForMissingAtEnd(List<BarChartEntry> sortedChartData) {
		int xAxisValuesCounter = 0;
		String lastXValue = "";
		for (BarChartEntry entry : sortedChartData) {
			if (!entry.getXAxisValue().equals(lastXValue)) {
				lastXValue = entry.getXAxisValue();
				xAxisValuesCounter++;
			}
		}
		for (; xAxisValuesCounter < possibleXAxisValues.size(); xAxisValuesCounter++) {
			sortedChartData.add(new FakeBarChartEntry(possibleXAxisValues.get(xAxisValuesCounter)));
		}
	}

	private void improveFakeBarChartEntries(List<BarChartEntry> sortedChartData) {
		BarChartEntry nonFakeEntry = findFirstNonFakeEntry(sortedChartData);
		if (nonFakeEntry == null) {
			return;
		}
		for (BarChartEntry entry : sortedChartData) {
			if (entry instanceof FakeBarChartEntry
					&& entry.getGroupFieldValue().equals(FieldValueFinder.DEFAULT_FIELD_VALUE)) {
				((FakeBarChartEntry) entry).copyGroupFieldValue(nonFakeEntry);
			}
		}
	}

	private BarChartEntry findFirstNonFakeEntry(List<BarChartEntry> sortedChartData) {
		for (BarChartEntry entry : sortedChartData) {
			if (!(entry instanceof FakeBarChartEntry)) {
				return entry;
			}
		}
		return null;
	}

}
