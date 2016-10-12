package com.tngtech.jira.plugins.gadget.data;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.atlassian.gzipfilter.org.apache.commons.lang.builder.HashCodeBuilder;

public class BarChartEntry {

	private final String xAxisValue;
	private final String groupFieldValue;
	private int value;

	public BarChartEntry(String xAxisValue, String groupFieldValue) {
		this.xAxisValue = xAxisValue;
		this.groupFieldValue = groupFieldValue;
		value = 1;
	}

	public void increaseValue() {
		value++;
	}

	public String getXAxisValue() {
		return xAxisValue;
	}

	public String getGroupFieldValue() {
		return groupFieldValue;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("At x = ");
		builder.append(getXAxisValue());
		builder.append(" with group value ");
		builder.append(getGroupFieldValue());
		builder.append(": ");
		builder.append(value);
		builder.append(" entries!");
		return builder.toString();
	}

	protected void setValue(int value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
