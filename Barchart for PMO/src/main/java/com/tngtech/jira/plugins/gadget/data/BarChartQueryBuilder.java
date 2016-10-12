package com.tngtech.jira.plugins.gadget.data;

import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.query.Query;
import com.tngtech.jira.plugins.utils.IdType;
import org.slf4j.LoggerFactory;

public class BarChartQueryBuilder {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(BarChartQueryBuilder.class);

	public BarChartQueryBuilder() {
	}

	public Query buildQuery(IdType projectOrFilterIdType, Long projectOrFilterId) {
		final JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		appendWhereClause(builder, projectOrFilterIdType, projectOrFilterId);
		return builder.buildQuery();
	}

	private void appendWhereClause(final JqlQueryBuilder builder, IdType projectOrFilterIdType, Long projectOrFilterId) {
		switch (projectOrFilterIdType) {
		case Project:
			builder.where().project(projectOrFilterId);
			break;
		case Filter:
			builder.where().savedFilter(projectOrFilterId.toString());
			break;
		default:
			log.error("Could not append where clause! Didn't recognize id type!");
			break;
		}
	}

}
