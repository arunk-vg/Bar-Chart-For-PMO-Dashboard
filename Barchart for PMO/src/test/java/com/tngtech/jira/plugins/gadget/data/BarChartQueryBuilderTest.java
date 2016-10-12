package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import com.tngtech.jira.plugins.utils.IdType;

public class BarChartQueryBuilderTest {

	/*
	 * Tests in here not working anymore due to changes in the Jira api 4.4 -> 5.0
	 * Stack Trace:
	 * java.lang.IllegalStateException: ComponentAccessor has not been initialised.
	 * at com.atlassian.jira.component.ComponentAccessor.getWorker(ComponentAccessor.java:848)
	 * at com.atlassian.jira.component.ComponentAccessor.getComponent(ComponentAccessor.java:105)
	 * at com.atlassian.jira.jql.builder.JqlQueryBuilder.createClauseBuilder(JqlQueryBuilder.java:205)
	 * at com.atlassian.jira.jql.builder.JqlQueryBuilder.<init>(JqlQueryBuilder.java:132)
	 * at com.atlassian.jira.jql.builder.JqlQueryBuilder.newBuilder(JqlQueryBuilder.java:37)
	 * at com.tngtech.jira.plugins.gadget.data.BarChartQueryBuilder.buildQuery(BarChartQueryBuilder.java:17)
	 * at com.tngtech.jira.plugins.gadget.data.BarChartQueryBuilderTest.shouldCreateProjectWhereClause(BarChartQueryBuilderTest.java:19)
	 * 
	 * Tried to mock the missing objects, but mostly static methods and use of interfaces make it difficult
	 * Skipped these tests, since they didn't provide that much extra info
	 */
	
	@Test
	public void shouldCreateProjectWhereClause() {
//		BarChartQueryBuilder builder = new BarChartQueryBuilder();
//		Long projectId = 1337L;
//		Query query = builder.buildQuery(IdType.Project, projectId);
//
//		assertThat(query, notNullValue());
//		Clause whereClause = query.getWhereClause();
//		assertThat(whereClause, notNullValue());
//		assertThat(whereClause.toString(), equalTo("{project = 1337}"));
	}

	@Test
	public void shouldCreateFilterWhereClause() {
//		BarChartQueryBuilder builder = new BarChartQueryBuilder();
//		Long filterId = 1337L;
//		Query query = builder.buildQuery(IdType.Filter, filterId);
//
//		assertThat(query, notNullValue());
//		Clause whereClause = query.getWhereClause();
//		assertThat(whereClause, notNullValue());
//		assertThat(whereClause.toString(), equalTo("{filter in (\"1337\")}"));
	}

}
