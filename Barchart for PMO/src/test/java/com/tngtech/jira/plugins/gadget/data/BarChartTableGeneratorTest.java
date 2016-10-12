package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tngtech.jira.plugins.utils.JiraUtils;

public class BarChartTableGeneratorTest {

	private JiraUtils jiraUtils;

	@Before
	public void mockJiraUtils() {
		jiraUtils = mock(JiraUtils.class);
		when(jiraUtils.getTranslatedText("gadget.barchart.view.table.sum")).thenReturn("Sum");
	}

	@Test
	public void shouldFindNoXValuesWithoutData() {
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, new ArrayList<BarChartEntry>());

		List<String> foundXValues = generator.getFoundXValues();

		assertThat(foundXValues, notNullValue());
		assertThat(foundXValues.size(), is(0));
	}

	@Test
	public void shouldFindNoOriginsWithoutData() {
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, new ArrayList<BarChartEntry>());

		List<String> foundOrigins = generator.getFoundGroupValues();

		assertThat(foundOrigins, notNullValue());
		assertThat(foundOrigins.size(), is(0));
	}

	@Test
	public void shouldFindRightXValues() {
		ArrayList<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		chartData.add(new BarChartEntry("X1", "Origin1"));
		chartData.add(new BarChartEntry("X2", "Origin1"));
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, chartData);

		List<String> foundXValues = generator.getFoundXValues();

		assertThat(foundXValues.size(), is(2));
		assertThat(foundXValues.contains("X1"), is(true));
		assertThat(foundXValues.contains("X2"), is(true));
	}

	@Test
	public void shouldFindRightGroupValues() {
		ArrayList<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		chartData.add(new BarChartEntry("X", "Origin1"));
		chartData.add(new BarChartEntry("X", "Origin2"));
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, chartData);

		List<String> foundOrigins = generator.getFoundGroupValues();

		assertThat(foundOrigins.size(), is(2));
		assertThat(foundOrigins.contains("Origin1"), is(true));
		assertThat(foundOrigins.contains("Origin2"), is(true));
	}

	@Test
	public void shouldReturnXValuesInInsertionOrder() {
		ArrayList<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		chartData.add(new BarChartEntry("CC", "Origin1"));
		chartData.add(new BarChartEntry("AA", "Origin1"));
		chartData.add(new BarChartEntry("BB", "Origin1"));
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, chartData);

		List<String> foundXValues = generator.getFoundXValues();

		assertThat(foundXValues.size(), is(3));
		assertThat(foundXValues.get(0), is("CC"));
		assertThat(foundXValues.get(1), is("AA"));
		assertThat(foundXValues.get(2), is("BB"));
	}

	@Test
	public void shouldNotModifyOrderOfGroupValues() {
		ArrayList<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		chartData.add(new BarChartEntry("X", "Origin2"));
		chartData.add(new BarChartEntry("X", "Origin1"));
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, chartData);

		List<String> foundOrigins = generator.getFoundGroupValues();

		assertThat(foundOrigins.size(), is(2));
		assertThat(foundOrigins.get(0), is("Origin2"));
		assertThat(foundOrigins.get(1), is("Origin1"));
	}

	@Test
	public void shouldCreateZerosTableWithoutData() {
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, new ArrayList<BarChartEntry>());

		List<List<String>> tableData = generator.extractTableData();

		assertThat(tableData, notNullValue());
		assertThat(tableData.size(), is(2));
		// 0th row: Header
		List<String> rowData = tableData.get(0);
		assertThat(rowData, notNullValue());
		assertThat(rowData.size(), is(1));
		assertThat(rowData.get(0), equalTo("Sum"));
		// 1st row: Empty sum row
		rowData = tableData.get(1);
		assertThat(rowData, notNullValue());
		assertThat(rowData.size(), is(1));
		assertThat(rowData.get(0), equalTo("0"));
	}

	@Test
	public void shouldReturnRightTableData() {
		ArrayList<BarChartEntry> chartData = new ArrayList<BarChartEntry>();
		chartData.add(new BarChartEntry("X1", "Origin1"));
		chartData.add(new BarChartEntry("X2", "Origin2"));
		BarChartEntry entry = new BarChartEntry("X3", "Origin2");
		entry.increaseValue();
		chartData.add(entry);
		chartData.add(new BarChartEntry("X2", "Origin1"));
		chartData.add(new BarChartEntry("X1", "Origin1"));
		BarChartTableGenerator generator = new BarChartTableGenerator(jiraUtils, chartData);

		List<List<String>> tableData = generator.extractTableData();

		assertThat(tableData, notNullValue());
		assertThat(tableData.size(), is(4));
		// 1th row: Origin1
		List<String> rowData = tableData.get(1);
		assertThat(rowData, notNullValue());
		assertThat(rowData.size(), is(4));
		assertThat(rowData.get(0), is("2"));
		assertThat(rowData.get(1), is("1"));
		assertThat(rowData.get(2), is("0"));
		assertThat(rowData.get(3), is("3"));
		// 2th row: Origin2
		rowData = tableData.get(2);
		assertThat(rowData, notNullValue());
		assertThat(rowData.size(), is(4));
		assertThat(rowData.get(0), is("0"));
		assertThat(rowData.get(1), is("1"));
		assertThat(rowData.get(2), is("2"));
		assertThat(rowData.get(3), is("3"));
		// 3nd row: sums
		rowData = tableData.get(3);
		assertThat(rowData, notNullValue());
		assertThat(rowData.size(), is(4));
		assertThat(rowData.get(0), is("2"));
		assertThat(rowData.get(1), is("2"));
		assertThat(rowData.get(2), is("2"));
		assertThat(rowData.get(3), is("6"));
	}

}
