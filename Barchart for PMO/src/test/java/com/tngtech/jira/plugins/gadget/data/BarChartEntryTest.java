package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BarChartEntryTest {

	@Test
	public void shouldHaveInitialValue() {
		BarChartEntry entry = new BarChartEntry("X", "");
		
		assertThat(entry.getValue(), is(1));
	}

	@Test
	public void shouldIncreaseValue() {
		BarChartEntry entry = new BarChartEntry("X", "");
		entry.increaseValue();
		
		assertThat(entry.getValue(), is(2));
	}

	@Test
	public void shouldEqualEquivalentEntry() {
		BarChartEntry realEntry = new BarChartEntry("X", "G");
		BarChartEntry sameEntry = new BarChartEntry("X", "G");
		
		assertThat(sameEntry, equalTo(realEntry));
	}

}
