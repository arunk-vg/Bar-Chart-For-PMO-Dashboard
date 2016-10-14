package com.tngtech.jira.plugins.gadget.chart;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;

import com.atlassian.gadgets.dashboard.Color.*;
import com.atlassian.jira.charts.jfreechart.ChartGenerator;
import com.atlassian.jira.charts.jfreechart.ChartHelper;
import com.atlassian.jira.charts.jfreechart.util.ChartUtil;
import com.atlassian.jira.util.velocity.VelocityRequestContext;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.query.QueryImpl;
import com.tngtech.jira.plugins.utils.JiraUtils;

public class BarChartGenerator implements ChartGenerator {

	private static final double MAXIMUM_BAR_WIDTH = 0.06;
	private final CategoryDataset dataset;
	private final JiraUtils jiraUtils;
    private final VelocityRequestContextFactory velocityRequestContextFactory;

	boolean tooltips;
	boolean urls;
	

	public BarChartGenerator(CategoryDataset dataset, JiraUtils jiraUtils,VelocityRequestContextFactory velocityRequestContextFactory) {
		this.dataset = dataset;
		this.jiraUtils = jiraUtils;
		this.velocityRequestContextFactory = velocityRequestContextFactory;
	}

	@Override
	public ChartHelper generateChart() {
		System.out.println("Inside default generateChart Method");
		return generateChart("", true,  null,null);
	}

	public ChartHelper generateChart(String xAxisFieldName, boolean legend, String imageMap,String imageMapName) {
		tooltips = true;
		urls = true;
		JFreeChart chart = ChartFactory.createStackedBarChart("", xAxisFieldName, jiraUtils
				.getTranslatedText("gadget.barchart.chart.countissues"), dataset, PlotOrientation.HORIZONTAL, legend,
				tooltips, urls);

		
		CategoryPlot plot = (CategoryPlot) chart.getPlot();		
		formatAxes(plot);
		setChartDefaults(chart, plot, jiraUtils.geti18nBean(),imageMap,imageMapName);		
		return new ChartHelper(chart);
	}
	

	private void formatAxes(CategoryPlot plot) {
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		System.out.println("FormatAxex YAXIS :  "+yAxis.getLabel());
		TickUnitSource units = NumberAxis.createIntegerTickUnits();
		//System.out.println(" TickUnitSource UNITS :  "+units);
		yAxis.setStandardTickUnits(units);
		yAxis.setAutoRange(true);
		yAxis.setAutoRangeIncludesZero(true);
		CategoryAxis xAxis = plot.getDomainAxis();
		System.out.println("FormatAxex XAXIS :  "+xAxis.getLabel());

		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	}
	private void setChartDefaults(JFreeChart chart, CategoryPlot plot, I18nBean bean, String imageMap,String imageMapName) {
		ChartUtil.setDefaults(chart, bean);
		//StackedBarRenderer renderer = new StackedBarRenderer();
		//CategoryItemRenderer renderer = new CustomRenderer(); 
		// set the color (r,g,b) or (r,g,b,a)
		//Color color = new Color(79, 129, 189);
		//renderer.setSeriesPaint(0, color);
		//plot.setRenderer(renderer);
		/*StackedBarRenderer render = new StackedBarRenderer();
		render.setMaximumBarWidth(MAXIMUM_BAR_WIDTH);
		plot.setRenderer(render);*/
		
		 String map = "";

	        ChartRenderingInfo info = new ChartRenderingInfo(
	            new StandardEntityCollection());

	      //  try {
	           /* ByteArrayOutputStream out = new ByteArrayOutputStream();
	            ChartUtilities.writeChartAsPNG(out, chart, 600, 400, info);*/

	            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
	                public String generateToolTipFragment(String arg0) {
	                    String toolTip = " title = \"value" + arg0 + "\"";
	                    return (toolTip);
	                }
	            };

	            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
	                public String generateURLFragment(String arg0) {
	                    final VelocityRequestContext velocityRequestContext = velocityRequestContextFactory.getJiraVelocityRequestContext();

	                    String address = velocityRequestContext.getCanonicalBaseUrl() + "/secure/IssueNavigator.jspa"

	                        + arg0 + "\"";
	                    return (address);
	                }
	            };

	            map = ChartUtilities.getImageMap("chart", info, tooltipConstructor,
	                urlConstructor);
	            
	           // out.close();
	       /* } catch (IOException e) {
	            System.out.println(e);
	        }*/
	        imageMap = map;
	        System.out.println(map);
	        
	        
		StackedBarRenderer renderer = new StackedBarRenderer();
		@SuppressWarnings("unchecked")
		List<String> ro = dataset.getRowKeys();
		for(String dat : ro)
		{
			System.out.println("Data Row Keys :"+dat);	
			
			if(dat.equals("Red"))
			{
				int redIndex =dataset.getRowIndex(dat);
//				System.out.println("Row Index : "+redIndex);
				renderer.setSeriesPaint(redIndex,Color.red );
			}
			else if(dat.equals("Yellow")){
				int redIndex =dataset.getRowIndex(dat);
//				System.out.println("Row Index : "+redIndex);
				renderer.setSeriesPaint(redIndex,Color.yellow );
			}
			else if(dat.equals("Green")){
				int redIndex =dataset.getRowIndex(dat);
//				System.out.println("Row Index : "+redIndex);
				renderer.setSeriesPaint(redIndex,Color.green );
			}
			else
			{
				int redIndex =dataset.getRowIndex(dat);
//				System.out.println("Row Index : "+redIndex);
				renderer.setSeriesPaint(redIndex,Color.black );
			}
			
		}	

		/*if (tooltips) {
	    	System.out.println("Boolean Tooltips : "+tooltips);
	        renderer.setBaseToolTipGenerator(
	                new StandardCategoryToolTipGenerator());
	    }
	    if (urls) {
	    	System.out.println("Boolean Urls : "+urls);
	        renderer.setBaseItemURLGenerator(
	                new StandardCategoryURLGenerator());
	    }*/
		
		renderer.setMaximumBarWidth(MAXIMUM_BAR_WIDTH);          
         plot.setRenderer(renderer);
        
	}

}