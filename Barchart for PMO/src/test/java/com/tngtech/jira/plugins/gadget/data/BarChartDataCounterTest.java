package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BarChartDataCounterTest {

	@Test
	public void shouldFindZeroEntries() {
		List<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		
		BarChartDataCounter dataCounter = new BarChartDataCounter(chartData);
		
		assertThat(dataCounter.countEntries(), is(0));
	}

	@Test
	public void shouldNotConsiderEmptyAndNullGroupFieldValues() {
		List<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		BarChartEntry entry1 = new BarChartEntry(null, "");
		chartData.add(entry1);
		BarChartEntry entry2 = new BarChartEntry(null, null);
		chartData.add(entry2);
		
		BarChartDataCounter dataCounter = new BarChartDataCounter(chartData);
		
		assertThat(dataCounter.countEntries(), is(0));
	}

	@Test
	public void shouldConsiderValuesOfEntries() {
		List<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		BarChartEntry entry1 = new BarChartEntry(null, "group");
		chartData.add(entry1);
		BarChartEntry entry2 = new BarChartEntry(null, "group");
		entry2.increaseValue();
		chartData.add(entry2);
		BarChartEntry entry3 = new BarChartEntry(null, "group");
		chartData.add(entry3);
		
		BarChartDataCounter dataCounter = new BarChartDataCounter(chartData);
		
		assertThat(dataCounter.countEntries(), is(4));
	}

}
