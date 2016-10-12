package com.tngtech.jira.plugins.gadget;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.annotations.VisibleForTesting;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.Log;

import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.charts.jfreechart.ChartHelper;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.tngtech.jira.plugins.gadget.chart.BarChartGenerator;
import com.tngtech.jira.plugins.gadget.data.BarChartDataCollector;
import com.tngtech.jira.plugins.gadget.data.BarChartDataCounter;
import com.tngtech.jira.plugins.gadget.data.BarChartEntry;
import com.tngtech.jira.plugins.gadget.data.BarChartIssueSearcher;
import com.tngtech.jira.plugins.gadget.data.BarChartQueryBuilder;
import com.tngtech.jira.plugins.gadget.data.BarChartTableGenerator;
import com.tngtech.jira.plugins.gadget.rest.Chart;
import com.tngtech.jira.plugins.utils.JiraUtils;
import org.slf4j.LoggerFactory;

@Path("/BarChart")
@Produces({ MediaType.APPLICATION_JSON })
public class BarChartGadget {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(BarChartGadget.class);

	private final JiraUtils jiraUtils;

	private static final int DEFAULT_HEIGHT = 600;
	private static final int DEFAULT_WIDTH = 650;

	private final ThreadLocal<BarChartIssueSearcher> issueSearcher = new ThreadLocal<BarChartIssueSearcher>();
	private final ThreadLocal<BarChartDataCollector> dataCollector = new ThreadLocal<BarChartDataCollector>();

	private final ThreadLocal<Integer> width = new ThreadLocal<Integer>();
	private final ThreadLocal<Integer> height = new ThreadLocal<Integer>();

	@VisibleForTesting
	public BarChartGadget(JiraUtils jiraUtils) {
		this.jiraUtils = jiraUtils;
	}

	@GET
	@Path("/GenerateChart")
	@AnonymousAllowed
	public Response getVersionsForProject(@QueryParam("projectOrFilterId") String projectOrFilterIdString,
			@QueryParam("axisField") String xAxisFieldIdString, @QueryParam("groupField") String groupFieldIdString,
			@QueryParam("width") String widthString, @QueryParam("height") String heightString) {
		try {
			BarChartIssueSearcher barChartIssueSeacher = new BarChartIssueSearcher(jiraUtils, new BarChartQueryBuilder());
			return getVersionsForProject(projectOrFilterIdString, xAxisFieldIdString, groupFieldIdString, widthString,
					heightString, barChartIssueSeacher);
		} catch (Exception e) {
			log.error("Error generating barchart", e);
			return Response.serverError().build();
		}
	}

	@VisibleForTesting
	public Response getVersionsForProject(String projectOrFilterIdString, String xAxisFieldIdString,
			String groupFieldIdString, String widthString, String heightString,
			BarChartIssueSearcher barChartIssueSeacher) {
		if (projectOrFilterIdString == null) {
			projectOrFilterIdString = "";
		}
		issueSearcher.set(barChartIssueSeacher);
		dataCollector.set(new BarChartDataCollector(jiraUtils));

		initializeChartSize(widthString, heightString);
		issueSearcher.get().extractDataFromIdString(projectOrFilterIdString);

		Chart chart = createRestClass(xAxisFieldIdString, groupFieldIdString);
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);

		return Response.ok(chart).cacheControl(cacheControl).build();
	}

	private void initializeChartSize(String widthString, String heightString) {
		// FIXME these checks should also be done in BarChartGadgetValidator
		if (widthString.matches("\\d+")) {
			width.set(Integer.valueOf(widthString));
		} else {
			width.set(DEFAULT_WIDTH);
		}
        if (heightString.matches("\\d+")) {
			height.set(Integer.valueOf(heightString));
		} else {
			height.set(DEFAULT_HEIGHT);
		}
	}

	private Chart createRestClass(String xAxisFieldIdString, String groupFieldIdString) {
		List<Issue> issues = issueSearcher.get().getAppropriateIssues();
		List<BarChartEntry> chartData = dataCollector.get().collectChartData(issues, xAxisFieldIdString,
				groupFieldIdString);
		String xAxisFieldName = dataCollector.get().getXAxisFieldName();
		boolean showLegend = !dataCollector.get().getGroupByFieldIsNone();

		String url = generateChart(chartData, xAxisFieldName, showLegend);
		BarChartTableGenerator tableGenerator = new BarChartTableGenerator(jiraUtils, chartData);
		List<String> origins = tableGenerator.getFoundGroupValues();
		List<List<String>> tableData = tableGenerator.extractTableData();

		String projectOrFilterName = issueSearcher.get().getProjectOrFilterName();
		BarChartDataCounter dataCounter = new BarChartDataCounter(chartData);
		String countEntries = Integer.toString(dataCounter.countEntries());
		String groupFieldName = dataCollector.get().getGroupFieldName();

		return new Chart(url, origins, tableData, projectOrFilterName, countEntries, groupFieldName);
	}

	private String generateChart(List<BarChartEntry> chartData, String xAxisFieldName, boolean legend) {
		CategoryDataset chartDataset = generateBarChartDataset(chartData);
		BarChartGenerator onTrackChartGenerator = new BarChartGenerator(chartDataset, jiraUtils);
		ChartHelper onTrackChartHelper = onTrackChartGenerator.generateChart(xAxisFieldName, legend);

		try {
			onTrackChartHelper.generate(width.get(), height.get());
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
		return onTrackChartHelper.getLocation();
	}

	private CategoryDataset generateBarChartDataset(List<BarChartEntry> chartData) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (BarChartEntry entry : chartData) {
			dataset.addValue(entry.getValue(), entry.getGroupFieldValue(), entry.getXAxisValue());
		}
		return dataset;
	}
}