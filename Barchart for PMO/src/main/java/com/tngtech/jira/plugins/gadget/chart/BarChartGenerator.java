package com.tngtech.jira.plugins.gadget.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;

import com.atlassian.jira.charts.jfreechart.ChartGenerator;
import com.atlassian.jira.charts.jfreechart.ChartHelper;
import com.atlassian.jira.charts.jfreechart.util.ChartUtil;
import com.atlassian.jira.web.bean.I18nBean;
import com.tngtech.jira.plugins.utils.JiraUtils;

public class BarChartGenerator implements ChartGenerator {

	private static final double MAXIMUM_BAR_WIDTH = 0.04;
	private final CategoryDataset dataset;
	private final JiraUtils jiraUtils;

	public BarChartGenerator(CategoryDataset dataset, JiraUtils jiraUtils) {
		this.dataset = dataset;
		this.jiraUtils = jiraUtils;
	}

	@Override
	public ChartHelper generateChart() {
		return generateChart("", true);
	}

	public ChartHelper generateChart(String xAxisFieldName, boolean legend) {
		boolean tooltips = true;
		boolean urls = true;
	//	System.out.println("GenerateChart Rest Resource xAxisFieldName : "+xAxisFieldName);
		JFreeChart chart = ChartFactory.createStackedBarChart("", xAxisFieldName, jiraUtils
				.getTranslatedText("gadget.barchart.chart.countissues"), dataset, PlotOrientation.HORIZONTAL, legend,
				tooltips, urls);
		
		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        KeyToGroupMap map = new KeyToGroupMap();
        map.getGroups();
       
        renderer.setSeriesToGroupMap(map); 
        
        renderer.setItemMargin(0.0);
		
		
		
		chart.setBorderVisible(true);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		formatAxes(plot);
		plot.setRenderer(renderer);
		setChartDefaults(chart, plot, jiraUtils.geti18nBean());
		return new ChartHelper(chart);
	}

	private void formatAxes(CategoryPlot plot) {
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		TickUnitSource units = NumberAxis.createIntegerTickUnits();
		yAxis.setStandardTickUnits(units);
		yAxis.setAutoRange(true);
		yAxis.setAutoRangeIncludesZero(true);
		CategoryAxis xAxis = plot.getDomainAxis();
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	}

	private void setChartDefaults(JFreeChart chart, CategoryPlot plot, I18nBean bean) {
		ChartUtil.setDefaults(chart, bean);
		StackedBarRenderer renderer = new StackedBarRenderer();
		renderer.setMaximumBarWidth(MAXIMUM_BAR_WIDTH);
		plot.setRenderer(renderer);
	}

}