package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.tngtech.jira.plugins.utils.IdType;
import com.tngtech.jira.plugins.utils.JiraUtils;

public class BarChartIssueSearcherTest {

	@Test
	public void shouldExtractProjectId() {
		BarChartIssueSearcher searcher = new BarChartIssueSearcher(null, null);
		searcher.extractDataFromIdString("project-12345");

		assertThat(searcher.getProjectOrFilterIdType(), is(IdType.Project));
		assertThat(searcher.getProjectOrFilterId().longValue(), is(12345L));
	}

	@Test
	public void shouldExtractFilterId() {
		BarChartIssueSearcher searcher = new BarChartIssueSearcher(null, null);
		searcher.extractDataFromIdString("filter-12345");

		assertThat(searcher.getProjectOrFilterIdType(), is(IdType.Filter));
		assertThat(searcher.getProjectOrFilterId().longValue(), is(12345L));
	}

	// Ignore following error logging from src code. Tests create error cases on purpose and check their handling!
	@Test
	public void shouldNotRecognizeId() {
		BarChartIssueSearcher searcher = new BarChartIssueSearcher(null, null);
		searcher.extractDataFromIdString("This error message is thrown as a test case. Please ignore this error message!");

		assertThat(searcher.getProjectOrFilterIdType(), is(IdType.Undefined));
		assertThat(searcher.getProjectOrFilterId(), nullValue());
	}

	@Test
	public void shouldReturnEmptyIssueListWithoutId() {
		BarChartIssueSearcher searcher = new BarChartIssueSearcher(null, null);
		List<Issue> foundIssues = searcher.getAppropriateIssues();

		assertThat(foundIssues, notNullValue());
		assertThat(foundIssues.size(), is(0));
	}

	@Test
	public void shouldReturnCorrectIssueList() throws SearchException {
		BarChartQueryBuilder queryBuilder = mock(BarChartQueryBuilder.class);
		Query query = mock(Query.class);
		when(queryBuilder.buildQuery(any(IdType.class), any(Long.class))).thenReturn(query);
		JiraUtils jiraUtils = mock(JiraUtils.class);
		List<Issue> issues = new ArrayList<Issue>();
		issues.add(mock(Issue.class));
		SearchResults searchResults = mock(SearchResults.class);
		when(searchResults.getIssues()).thenReturn(issues);
		when(jiraUtils.getSearchResults(any(Query.class), any(PagerFilter.class))).thenReturn(searchResults);

		BarChartIssueSearcher searcher = new BarChartIssueSearcher(jiraUtils, queryBuilder);
		searcher.extractDataFromIdString("project-12345");

		List<Issue> foundIssues = searcher.getAppropriateIssues();

		assertThat(foundIssues, equalTo(issues));
	}

	@Test
	public void shouldReturnProjectName() {
		JiraUtils jiraUtils = mock(JiraUtils.class);
		Project project = mock(Project.class);
		String projectName = "project_name";
		when(project.getName()).thenReturn(projectName);
		when(jiraUtils.getProjectById(any(Long.class))).thenReturn(project);

		BarChartIssueSearcher searcher = new BarChartIssueSearcher(jiraUtils, null);
		searcher.extractDataFromIdString("project-12345");
		String foundName = searcher.getProjectOrFilterName();

		assertThat(foundName, equalTo(projectName));
	}

	@Test
	public void shouldReturnFilterName() {
		JiraUtils jiraUtils = mock(JiraUtils.class);
		SearchRequest searchRequest = mock(SearchRequest.class);
		String filterName = "filter_name";
		when(searchRequest.getName()).thenReturn(filterName);
		when(jiraUtils.getSearchRequestForFilter(any(Long.class))).thenReturn(searchRequest);

		BarChartIssueSearcher searcher = new BarChartIssueSearcher(jiraUtils, null);
		searcher.extractDataFromIdString("filter-12345");
		String foundName = searcher.getProjectOrFilterName();

		assertThat(foundName, equalTo(filterName));
	}

	@Test
	public void shouldReturnEmptyNameWithoutId() {
		BarChartIssueSearcher searcher = new BarChartIssueSearcher(null, null);
		String foundName = searcher.getProjectOrFilterName();

		assertThat(foundName, notNullValue());
		assertThat(foundName.isEmpty(), is(true));
	}

}
