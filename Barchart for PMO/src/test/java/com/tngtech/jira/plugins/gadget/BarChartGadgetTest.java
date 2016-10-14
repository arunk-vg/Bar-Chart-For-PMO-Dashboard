package com.tngtech.jira.plugins.gadget;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import com.atlassian.jira.charts.util.ChartUtils;
import com.atlassian.jira.io.TempFileFactory;
import com.atlassian.jira.mock.component.MockComponentWorker;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.bean.I18nBean;
import com.tngtech.jira.plugins.gadget.BarChartGadget;
import com.tngtech.jira.plugins.gadget.data.BarChartIssueSearcher;
import com.tngtech.jira.plugins.gadget.data.BarChartIssueSearcherDummy;
import com.tngtech.jira.plugins.gadget.rest.Chart;
import com.tngtech.jira.plugins.utils.JiraUtils;

public class BarChartGadgetTest {

	private Long projectId = 10000L;
	private String projectOrFilterIdString = "project-" + projectId;
	private String projectName = "ProjectName";
	private String xAxisFieldIdString = "00042";
	private String groupFieldIdString = "01337";
	private String groupFieldName = "GroupFieldName";
	private String widthString = "800";
	private String heightString = "600";

	@Before
	public void setUp() {
		TempFileFactory tempFileFactory = mock(TempFileFactory.class);
		ChartUtils chartUtils = mock(ChartUtils.class);
		new MockComponentWorker()
				.addMock(TempFileFactory.class, tempFileFactory)
				.addMock(ChartUtils.class, chartUtils)
				.init();
	}

	@Test
	public void testGadgetOutput() throws SearchException {
		List<Issue> issues = mockIssues();
		JiraUtils jiraUtils = mockJiraUtils(issues);
		BarChartIssueSearcher barChartIssueSearcher = mockBarChartIssueSearcher(jiraUtils, issues);

		BarChartGadget gadget = new BarChartGadget(jiraUtils, null);
		Response response = gadget.getVersionsForProject(projectOrFilterIdString, xAxisFieldIdString,
				groupFieldIdString, widthString, heightString, barChartIssueSearcher);

		checkResponse(response);
	}

	private JiraUtils mockJiraUtils(List<Issue> issues) throws SearchException {
		JiraUtils jiraUtils = mock(JiraUtils.class);

		I18nBean i18nBean = mock(I18nBean.class);
		when(jiraUtils.geti18nBean()).thenReturn(i18nBean);

		CustomField xAxisField = mockXAxisField(issues);
		CustomField groupField = mockGroupField(issues);
		when(jiraUtils.getCustomFieldById(Long.valueOf(xAxisFieldIdString))).thenReturn(xAxisField);
		when(jiraUtils.getCustomFieldById(Long.valueOf(groupFieldIdString))).thenReturn(groupField);

		List<Option> customFieldOptions = mockCustomFieldOptions(xAxisField, groupField);
		when(jiraUtils.getAllCustomFieldOptions()).thenReturn(customFieldOptions);

		List<CustomField> customFields = new ArrayList<CustomField>();
		customFields.add(xAxisField);
		customFields.add(groupField);
		when(jiraUtils.getAllCustomFields()).thenReturn(customFields);

		Project project = mockProject();
		when(jiraUtils.getProjectById(projectId)).thenReturn(project);

		when(jiraUtils.getTranslatedText("gadget.barchart.view.table.sum")).thenReturn("Sum");

		return jiraUtils;
	}

	private List<Issue> mockIssues() {
		List<Issue> issues = new ArrayList<Issue>();
		for (int i = 0; i < 10; i++) {
			issues.add(mockIssue());
		}
		return issues;
	}

	private Issue mockIssue() {
		Issue issue = mock(Issue.class);
		return issue;
	}

	private CustomField mockXAxisField(List<Issue> issues) {
		CustomField xAxisField = mock(CustomField.class);
		for (int i = 0; i < issues.size(); i++) {
			when(xAxisField.getValue(issues.get(i))).thenReturn("X" + i);
		}
		return xAxisField;
	}

	private CustomField mockGroupField(List<Issue> issues) {
		CustomField groupField = mock(CustomField.class);
		for (int i = 0; i < issues.size(); i++) {
			int nr = i % 3;
			when(groupField.getValue(issues.get(i))).thenReturn("G" + nr);
		}
		when(groupField.getName()).thenReturn(groupFieldName);
		return groupField;
	}

	private List<Option> mockCustomFieldOptions(CustomField xAxisField, CustomField groupField) {
		List<Option> customFieldOptions = new ArrayList<Option>();

		FieldConfig xAxisConfig = mock(FieldConfig.class);
		when(xAxisConfig.getCustomField()).thenReturn(xAxisField);
		for (int i = 0; i < 10; i++) {
			Option o = mock(Option.class);
			when(o.getValue()).thenReturn("X" + i);
			when(o.getRelatedCustomField()).thenReturn(xAxisConfig);
			customFieldOptions.add(o);
		}

		FieldConfig groupConfig = mock(FieldConfig.class);
		when(groupConfig.getCustomField()).thenReturn(groupField);
		for (int i = 0; i < 3; i++) {
			Option o = mock(Option.class);
			when(o.getValue()).thenReturn("G" + i);
			when(o.getRelatedCustomField()).thenReturn(groupConfig);
			customFieldOptions.add(o);
		}

		return customFieldOptions;
	}

	private Project mockProject() {
		Project project = mock(Project.class);
		when(project.getName()).thenReturn(projectName);
		return project;
	}

	private BarChartIssueSearcher mockBarChartIssueSearcher(JiraUtils jiraUtils, List<Issue> issues) {
		BarChartIssueSearcher barChartIssueSearcher = new BarChartIssueSearcherDummy(jiraUtils, issues);
		return barChartIssueSearcher;
	}

	private void checkResponse(Response response) {
		Chart chart = (Chart) response.getEntity();
		assertThat(chart.countIssues, equalTo("10"));
		checkChartData(chart.data);
		assertThat(chart.groupBy, equalTo(groupFieldName));
		assertThat(chart.groupValues.size(), is(3));
		for (int i = 0; i < chart.groupValues.size(); i++) {
			assertThat(chart.groupValues.get(i), equalTo("G" + i));
		}
		assertThat(chart.projectOrFilterName, equalTo(projectName));
		assertThat(chart.url, notNullValue());
		assertThat(chart.url.isEmpty(), is(false));
	}

	private void checkChartData(List<List<String>> data) {
		// [[X0, X1, X2, X3, X4, X5, X6, X7, X8, X9, Summe],
		// [1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 4],
		// [0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 3],
		// [0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 3],
		// [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10]]
		System.err.println(data);
		assertThat(data.size(), is(5));
		// Check Header
		for (int i = 0; i < 10; i++) {
			assertThat(data.get(0).get(i), equalTo("X" + i));
		}
		assertThat(data.get(0).get(10), is("Sum"));
		// Check G0, G1, G2
		for (int j = 1; j < 4; j++) {
			for (int i = 0; i < 10; i++) {
				int val = (i + 4 - j) % 3 == 0 ? 1 : 0;
				assertThat(data.get(j).get(i), is(Integer.toString(val)));
			}
			int val = j == 1 ? 4 : 3;
			assertThat(data.get(j).get(10), is(Integer.toString(val)));
		}
		// Check Sum
		for (int i = 0; i < 10; i++) {
			assertThat(data.get(4).get(i), equalTo("1"));
		}
		assertThat(data.get(4).get(10), is("10"));

	}

}
