package com.tngtech.jira.plugins.gadget.data;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.atlassian.gzipfilter.org.apache.commons.lang.builder.HashCodeBuilder;
import com.tngtech.jira.plugins.utils.fields.FieldValueFinder;

public class FakeBarChartEntry extends BarChartEntry {

	private String fakeGroupFieldValue = FieldValueFinder.DEFAULT_FIELD_VALUE;

	public FakeBarChartEntry(String xAxisValue, String groupFieldValue) {
		super(xAxisValue, "");
		setValue(0);
		fakeGroupFieldValue = groupFieldValue;
	}

	public FakeBarChartEntry(String xAxisValue) {
		super(xAxisValue, "");
		setValue(0);
	}

	public void copyGroupFieldValue(BarChartEntry entry) {
		fakeGroupFieldValue = entry.getGroupFieldValue();
	}

	@Override
	public String getGroupFieldValue() {
		return fakeGroupFieldValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Fake Entry at x = ");
		builder.append(getXAxisValue());
		builder.append(" with fake group value ");
		builder.append(getGroupFieldValue());
		return builder.toString();
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
