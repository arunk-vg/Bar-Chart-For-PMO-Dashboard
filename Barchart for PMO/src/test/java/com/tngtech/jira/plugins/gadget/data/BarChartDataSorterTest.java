package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tngtech.jira.plugins.utils.fields.FieldValueFinder;

// TODO check test class. eventually implement more test cases for each sorting case
public class BarChartDataSorterTest {

	private FieldValueFinder mockFieldValueFinder() {
		FieldValueFinder finder = mock(FieldValueFinder.class);
		return finder;
	}

	@Test
	public void shouldCreateFakeDataEntriesForXValuesBefore() {
		BarChartDataSorter sorter = mockSorter(Arrays.asList("group"));
		List<BarChartEntry> entries = new ArrayList<BarChartEntry>();
		entries.add(new BarChartEntry("X3", "group"));

		List<BarChartEntry> enrichedEntries = sorter.sortAndEnrichChartData("-1", "-1", entries);

		assertThat(enrichedEntries, notNullValue());
		assertThat(enrichedEntries.size(), is(3));
		for (int i = 0; i < 3; i++) {
			assertBarChartEntry(enrichedEntries.get(i), i + 1, i != 2);
		}
	}

	@Test
	public void shouldCreateFakeDataEntriesForGroupBefore() {
		BarChartDataSorter sorter = mockSorter(Arrays.asList("group1", "group2", "group3"));
		List<BarChartEntry> entries = new ArrayList<BarChartEntry>();
		entries.add(new BarChartEntry("X1", "group2"));
		entries.add(new BarChartEntry("X2", "group1"));
		entries.add(new BarChartEntry("X3", "group3"));

		List<BarChartEntry> enrichedEntries = sorter.sortAndEnrichChartData("-1", "-1", entries);

		assertThat(enrichedEntries, notNullValue());
		assertThat(enrichedEntries.size(), is(6));
		for (int i = 0; i < 3; i++) {
			assertBarChartEntry(enrichedEntries.get(i), "group" + (i + 1), 1, true);
		}
		assertBarChartEntry(enrichedEntries.get(3), "group2", 1, false);
		assertBarChartEntry(enrichedEntries.get(4), "group1", 2, false);
		assertBarChartEntry(enrichedEntries.get(5), "group3", 3, false);
	}

	@Test
	public void shouldCreateFakeDataEntriesAfter() {
		BarChartDataSorter sorter = mockSorter(Arrays.asList("group"));
		List<BarChartEntry> entries = new ArrayList<BarChartEntry>();
		entries.add(new BarChartEntry("X1", "group"));

		List<BarChartEntry> enrichedEntries = sorter.sortAndEnrichChartData("-1", "-1", entries);

		assertThat(enrichedEntries, notNullValue());
		assertThat(enrichedEntries.size(), is(4));
		assertBarChartEntry(enrichedEntries.get(0), 1, true);
		for (int i = 1; i < 4; i++) {
			assertBarChartEntry(enrichedEntries.get(i), i, i != 1);
		}
	}

	@Test
	public void shouldSetFakeGroupsToRealOne() {
		BarChartDataSorter sorter = mockSorter(Arrays.asList("group"));
		List<BarChartEntry> entries = new ArrayList<BarChartEntry>();
		entries.add(new BarChartEntry("X2", "group"));

		List<BarChartEntry> enrichedEntries = sorter.sortAndEnrichChartData("-1", "-1", entries);

		assertThat(enrichedEntries, notNullValue());
		assertThat(enrichedEntries.size(), is(3));
		for (int i = 0; i < 3; i++) {
			assertBarChartEntry(enrichedEntries.get(i), i + 1, i != 1);
		}
	}

	@Test
	public void shouldSortByXValue() {
		BarChartDataSorter sorter = mockSorter(Arrays.asList("group"));
		List<BarChartEntry> entries = new ArrayList<BarChartEntry>();
		entries.add(new BarChartEntry("X2", "group"));
		entries.add(new BarChartEntry("X3", "group"));
		entries.add(new BarChartEntry("X1", "group"));

		List<BarChartEntry> enrichedEntries = sorter.sortAndEnrichChartData("-1", "-1", entries);

		assertThat(enrichedEntries, notNullValue());
		assertThat(enrichedEntries.size(), is(4));
		assertBarChartEntry(enrichedEntries.get(0), 1, true);
		for (int i = 1; i < 4; i++) {
			assertBarChartEntry(enrichedEntries.get(i), i, false);
		}
	}

	@Test
	public void shouldSortByXValueWithoutPreconfig() {
		FieldValueFinder finder = mockFieldValueFinder();
		when(finder.getPossibleXAxisValues("-1")).thenReturn(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST);
		BarChartDataSorter sorter = mockSorter(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST, Arrays.asList("group"));

		List<BarChartEntry> entries = new ArrayList<BarChartEntry>();
		entries.add(new BarChartEntry("X2", "group"));
		entries.add(new BarChartEntry("X3", "group"));
		entries.add(new BarChartEntry("X1", "group"));

		List<BarChartEntry> enrichedEntries = sorter.sortAndEnrichChartData("-1", "-1", entries);
		assertThat(enrichedEntries, notNullValue());
		assertThat(enrichedEntries.size(), is(4));
		assertBarChartEntry(enrichedEntries.get(0), 1, true);
		for (int i = 1; i < 4; i++) {
			assertBarChartEntry(enrichedEntries.get(i), i, false);
		}
	}

	private void assertBarChartEntry(BarChartEntry entry, String xAxisValue, boolean fake) {
		assertThat(entry.getXAxisValue(), equalTo(xAxisValue));
		assertThat(entry.getValue(), equalTo(Integer.valueOf(fake ? 0 : 1)));
		assertThat(entry instanceof FakeBarChartEntry, is(fake));
		assertThat(entry.getGroupFieldValue(), equalTo("group"));
	}

	private void assertBarChartEntry(BarChartEntry entry, int nr, boolean fake) {
		assertBarChartEntry(entry, "X" + Integer.toString(nr), fake);
	}

	private void assertBarChartEntry(BarChartEntry entry, String groupValue, int nr, boolean fake) {
		assertThat(entry.getXAxisValue(), equalTo("X" + Integer.toString(nr)));
		assertThat(entry.getGroupFieldValue(), equalTo(groupValue));
		assertThat(entry instanceof FakeBarChartEntry, is(fake));
		assertThat(entry.getValue(), equalTo(Integer.valueOf(fake ? 0 : 1)));
	}

	public BarChartDataSorter mockSorter(List<String> groupValues) {
		List<String> xValues = new ArrayList<String>(3);
		for (int i = 1; i <= 3; i++) {
			xValues.add("X" + Integer.toString(i));
		}
		return mockSorter(xValues, groupValues);
	}

	public BarChartDataSorter mockSorter(List<String> xAxisValues, List<String> groupValues) {
		FieldValueFinder finder = mockFieldValueFinder();
		when(finder.getPossibleXAxisValues("-1")).thenReturn(xAxisValues);
		when(finder.getPossibleGroupFieldValues("-1")).thenReturn(groupValues);
		return new BarChartDataSorter(finder);
	}

}
