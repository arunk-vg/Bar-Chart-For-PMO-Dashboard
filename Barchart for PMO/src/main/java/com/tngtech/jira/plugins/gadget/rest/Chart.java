package com.tngtech.jira.plugins.gadget.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Chart {
	@XmlElement
	public String url;
	@XmlElement
	public List<String> groupValues;
	@XmlElement
	public List<List<String>> data;
	@XmlElement
	public String projectOrFilterName;
	@XmlElement
	public String countIssues;
	@XmlElement
	public String groupBy;
	@XmlElement
    private String imageMap;
    @XmlElement
    private String imageMapName;

	public Chart(String url, List<String> groupValues, List<List<String>> data, String projectOrFilterName,
			String countIssues, String groupBy, String imageMap, String imageMapName) {
		this.url = url;
		this.groupValues = groupValues;
		this.data = data;
		this.projectOrFilterName = projectOrFilterName;
		this.countIssues = countIssues;
		this.groupBy = groupBy;
		this.imageMap = imageMap;
		this.imageMapName = imageMapName;
	}

	@SuppressWarnings("unused") public Chart() {}
}