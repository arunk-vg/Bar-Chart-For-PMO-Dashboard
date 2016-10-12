package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.atlassian.jira.issue.Issue;
import com.tngtech.jira.plugins.utils.fields.FieldValueFinder;

public class BarChartDataCollectorTest {

	@Test
	public void shouldHandleNoIssues() {
		FieldValueFinder finder = mockFieldValueFinder();
		BarChartDataCollector collector = new BarChartDataCollector(finder);

		List<BarChartEntry> collectedData = collector.collectChartData(null, null, null);

		assertEmptyEntryList(collectedData);
		List<Issue> issues = new ArrayList<Issue>();
		collectedData = collector.collectChartData(issues, null, null);
		assertEmptyEntryList(collectedData);
	}

	@Test
	public void shouldHandleNoXValues() {
		Issue issue = mock(Issue.class);
		List<Issue> issues = new ArrayList<Issue>();
		Map<Issue, String> issueXValuePairs = new HashMap<Issue, String>();
		issueXValuePairs.put(issue, null);
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs);
		BarChartDataCollector collector = new BarChartDataCollector(finder);
		issues.add(issue);

		List<BarChartEntry> collectedData = collector.collectChartData(issues, "-1", "-1");

		assertThat(collectedData, notNullValue());
		for (BarChartEntry entry : collectedData)
			assertThat(entry.getXAxisValue(), notNullValue());
	}

	@Test
	public void shouldSaveData() {
		String xValue1 = "X1";
		String group1 = "Group1";
		String xValue2 = "X2";
		String group2 = "Group2";

		Map<Issue, String> issueXValuePairs = new HashMap<Issue, String>();
		Map<Issue, String> issueGroupPairs = new HashMap<Issue, String>();
		Issue issue1 = mock(Issue.class);
		issueXValuePairs.put(issue1, xValue1);
		issueGroupPairs.put(issue1, group1);
		Issue issue2 = mock(Issue.class);
		issueXValuePairs.put(issue2, xValue2);
		issueGroupPairs.put(issue2, group2);

		List<BarChartEntry> sortedEntries = new ArrayList<BarChartEntry>();
		sortedEntries.add(new BarChartEntry(xValue1, group1));
		sortedEntries.add(new BarChartEntry(xValue2, group2));
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs, issueGroupPairs);
		BarChartDataSorter sorter = mockBarChartDataSorter(sortedEntries);
		BarChartDataCollector collector = new BarChartDataCollector(finder, sorter);

		List<Issue> issues = new ArrayList<Issue>();
		issues.add(issue1);
		issues.add(issue2);

		List<BarChartEntry> collectedData = collector.collectChartData(issues, "-1", "-1");

		assertThat(collectedData, equalTo(sortedEntries));
	}

	@Test
	public void shouldAddData() {
		String xValue = "X";
		String group = "Group";

		Map<Issue, String> issueXValuePairs = new HashMap<Issue, String>();
		Map<Issue, String> issueGroupPairs = new HashMap<Issue, String>();
		Issue issue1 = mock(Issue.class);
		issueXValuePairs.put(issue1, xValue);
		issueGroupPairs.put(issue1, group);
		Issue issue2 = mock(Issue.class);
		issueXValuePairs.put(issue2, xValue);
		issueGroupPairs.put(issue2, group);

		List<BarChartEntry> sortedEntries = new ArrayList<BarChartEntry>();
		BarChartEntry barChartEntry = new BarChartEntry(xValue, group);
		barChartEntry.increaseValue();
		sortedEntries.add(barChartEntry);
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs, issueGroupPairs);
		BarChartDataSorter sorter = mockBarChartDataSorter(sortedEntries);
		BarChartDataCollector collector = new BarChartDataCollector(finder, sorter);

		List<Issue> issues = new ArrayList<Issue>();
		issues.add(issue1);
		issues.add(issue2);

		List<BarChartEntry> collectedData = collector.collectChartData(issues, "-1", "-1");

		assertThat(collectedData, equalTo(sortedEntries));
	}

	@Test
	public void shouldStackData() {
		String xValue = "X";
		String group1 = "Group1";
		String group2 = "Group2";

		Map<Issue, String> issueXValuePairs = new HashMap<Issue, String>();
		Map<Issue, String> issueGroupPairs = new HashMap<Issue, String>();
		Issue issue1 = mock(Issue.class);
		issueXValuePairs.put(issue1, xValue);
		issueGroupPairs.put(issue1, group1);
		Issue issue2 = mock(Issue.class);
		issueXValuePairs.put(issue2, xValue);
		issueGroupPairs.put(issue2, group2);

		List<BarChartEntry> sortedEntries = new ArrayList<BarChartEntry>();
		sortedEntries.add(new BarChartEntry(xValue, group1));
		sortedEntries.add(new BarChartEntry(xValue, group2));
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs, issueGroupPairs);
		BarChartDataSorter sorter = mockBarChartDataSorter(sortedEntries);
		BarChartDataCollector collector = new BarChartDataCollector(finder, sorter);

		List<Issue> issues = new ArrayList<Issue>();
		issues.add(issue1);
		issues.add(issue2);

		List<BarChartEntry> collectedData = collector.collectChartData(issues, "-1", "-1");

		assertThat(collectedData, equalTo(sortedEntries));
	}

	@Test
	public void shouldSortData() {
		String xValue = "X";
		String group2 = "Group2";
		String group1 = "Group1";

		Map<Issue, String> issueXValuePairs = new HashMap<Issue, String>();
		Map<Issue, String> issueGroupPairs = new HashMap<Issue, String>();
		Issue issue2 = mock(Issue.class);
		issueXValuePairs.put(issue2, xValue);
		issueGroupPairs.put(issue2, group2);
		Issue issue1 = mock(Issue.class);
		issueXValuePairs.put(issue1, xValue);
		issueGroupPairs.put(issue1, group1);

		List<BarChartEntry> sortedEntries = new ArrayList<BarChartEntry>();
		sortedEntries.add(new BarChartEntry(xValue, group1));
		sortedEntries.add(new BarChartEntry(xValue, group2));
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs, issueGroupPairs);
		BarChartDataSorter sorter = mockBarChartDataSorter(sortedEntries);
		BarChartDataCollector collector = new BarChartDataCollector(finder, sorter);

		List<Issue> issues = new ArrayList<Issue>();
		issues.add(issue1);
		issues.add(issue2);

		List<BarChartEntry> collectedData = collector.collectChartData(issues, "-1", "-1");

		assertThat(collectedData, equalTo(sortedEntries));
	}

	@Test
	public void shouldSaveMetaData() {
		String xValue = "X";
		String group = "Group";
		String lastFieldName = "LastFieldName";

		Map<Issue, String> issueXValuePairs = new HashMap<Issue, String>();
		Map<Issue, String> issueGroupPairs = new HashMap<Issue, String>();
		Issue issue1 = mock(Issue.class);
		issueXValuePairs.put(issue1, xValue);
		issueGroupPairs.put(issue1, group);
		Boolean lastFieldIsNone = false;

		List<BarChartEntry> sortedEntries = new ArrayList<BarChartEntry>();
		sortedEntries.add(new BarChartEntry(xValue, group));
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs, issueGroupPairs, lastFieldName,
				lastFieldIsNone);
		BarChartDataSorter sorter = mockBarChartDataSorter(sortedEntries);
		BarChartDataCollector collector = new BarChartDataCollector(finder, sorter);

		List<Issue> issues = new ArrayList<Issue>();
		issues.add(issue1);
		collector.collectChartData(issues, "-1", "-1");

		assertThat(collector.getGroupFieldName(), is(lastFieldName));
		assertThat(collector.getGroupByFieldIsNone(), is(lastFieldIsNone));
		assertThat(collector.getXAxisFieldName(), is(lastFieldName));
	}

	private void assertEmptyEntryList(List<BarChartEntry> collectedData) {
		assertThat(collectedData, notNullValue());
		assertThat(collectedData.size(), is(0));
	}

	private FieldValueFinder mockFieldValueFinder() {
		FieldValueFinder finder = mock(FieldValueFinder.class);
		return finder;
	}

	private FieldValueFinder mockFieldValueFinder(Map<Issue, String> issueXValuePairs) {
		FieldValueFinder finder = mockFieldValueFinder();
		for (Map.Entry<Issue, String> entry : issueXValuePairs.entrySet()) {
			List<String> values = Arrays.asList(entry.getValue());
			when(finder.findXAxisFieldValues(eq(entry.getKey()), anyString())).thenReturn(values);
		}
		return finder;
	}

	private FieldValueFinder mockFieldValueFinder(Map<Issue, String> issueXValuePairs,
			Map<Issue, String> issueGroupFieldValuePairs) {
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs);
		for (Map.Entry<Issue, String> entry : issueGroupFieldValuePairs.entrySet()) {
			List<String> values = ImmutableList.of(entry.getValue());
			when(finder.findGroupFieldValues(eq(entry.getKey()), anyString())).thenReturn(values);
		}
		return finder;
	}

	private FieldValueFinder mockFieldValueFinder(Map<Issue, String> issueXValuePairs,
			Map<Issue, String> issueGroupFieldValuePairs, String lastFieldName, Boolean groupFieldIsNone) {
		FieldValueFinder finder = mockFieldValueFinder(issueXValuePairs, issueGroupFieldValuePairs);
		when(finder.getLastFieldName()).thenReturn(lastFieldName);
		when(finder.getLastFieldWasNone()).thenReturn(groupFieldIsNone);
		return finder;
	}

	@SuppressWarnings("unchecked")
	private BarChartDataSorter mockBarChartDataSorter(List<BarChartEntry> sortedEntries) {
		BarChartDataSorter sorter = mock(BarChartDataSorter.class);
		when(sorter.sortAndEnrichChartData(anyString(), anyString(), anyList())).thenAnswer(
				new Answer<List<BarChartEntry>>() {
					@Override
					public List<BarChartEntry> answer(InvocationOnMock invocation) throws Throwable {
						Object[] args = invocation.getArguments();
						return (List<BarChartEntry>) args[2];
					}
				});
		return sorter;
	}

}
