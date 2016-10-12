package com.tngtech.jira.plugins.gadget.data;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.tngtech.jira.plugins.utils.IdType;
import com.tngtech.jira.plugins.utils.JiraUtils;
import org.slf4j.LoggerFactory;

public class BarChartIssueSearcher {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(BarChartIssueSearcher.class);

	private IdType projectOrFilterIdType = IdType.Undefined;
	private Long projectOrFilterId = null;

	private final JiraUtils jiraUtils;
	private final BarChartQueryBuilder queryBuilder;

	public BarChartIssueSearcher(JiraUtils jiraUtils, BarChartQueryBuilder queryBuilder) {
		this.jiraUtils = jiraUtils;
		this.queryBuilder = queryBuilder;
	}

	public void extractDataFromIdString(String projectOrFilterIdString) {
		projectOrFilterIdType = getIdType(projectOrFilterIdString);
		if (projectOrFilterIdType == IdType.Undefined) {
			log.error("Could not resolve ID Type from given value: {}", projectOrFilterIdString);
			return;
		}
		projectOrFilterId = extractId(projectOrFilterIdString);
	}

	private IdType getIdType(String projectOrFilterIdString) {
		if (projectOrFilterIdString.matches("project-\\d+")) {
			return IdType.Project;
		}
		if (projectOrFilterIdString.matches("filter-\\d+")) {
			return IdType.Filter;
		}
		return IdType.Undefined;
	}

	private Long extractId(String projectOrFilterIdString) {
		String extractedId = projectOrFilterIdString.replaceAll("[^\\d]", "");
		if (extractedId.isEmpty()) {
			return 0L;
		}
		return Long.valueOf(extractedId);
	}

	public List<Issue> getAppropriateIssues() {
		if (projectOrFilterIdType == IdType.Undefined) {
			return new ArrayList<Issue>();
		}
		try {
			Query query = queryBuilder.buildQuery(projectOrFilterIdType, projectOrFilterId);
			final SearchResults results = jiraUtils.getSearchResults(query, PagerFilter.getUnlimitedFilter());
			return results.getIssues();
		} catch (SearchException e) {
			log.error("Error running search", e);
		}
		return new ArrayList<Issue>();
	}

	public String getProjectOrFilterName() {
		switch (projectOrFilterIdType) {
		case Project:
			return getProjectName();
		case Filter:
			return getFilterName();
		}
		return "";
	}

	private String getProjectName() {
		Project project = jiraUtils.getProjectById(projectOrFilterId);
		if (project == null) {
			log.debug("project with id '{}' does not exist or is not viewable by user", projectOrFilterId);
			return "";
		}
		return project.getName();
	}

	private String getFilterName() {
		SearchRequest searchRequest = jiraUtils.getSearchRequestForFilter(projectOrFilterId);
		if (searchRequest == null) {
			log.debug("filter with id '{}' does not exist or is not viewable by user", projectOrFilterId);
			return "";
		}
		return searchRequest.getName();
	}

	public IdType getProjectOrFilterIdType() {
		return projectOrFilterIdType;
	}

	public Long getProjectOrFilterId() {
		return projectOrFilterId;
	}

}
