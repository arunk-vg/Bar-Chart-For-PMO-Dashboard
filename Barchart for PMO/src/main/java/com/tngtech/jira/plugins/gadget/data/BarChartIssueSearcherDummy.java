package com.tngtech.jira.plugins.gadget.data;

import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.tngtech.jira.plugins.utils.JiraUtils;

/*!
 * Class needed for the integration test
 */
public class BarChartIssueSearcherDummy extends BarChartIssueSearcher {

	private final List<Issue> issues;

	public BarChartIssueSearcherDummy(JiraUtils jiraUtils, List<Issue> issues) {
		super(jiraUtils, null);
		this.issues = issues;
	}

	@Override
	public List<Issue> getAppropriateIssues() {
		return issues;
	}

}
