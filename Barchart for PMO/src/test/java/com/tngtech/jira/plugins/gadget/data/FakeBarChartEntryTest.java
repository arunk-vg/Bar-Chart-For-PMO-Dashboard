package com.tngtech.jira.plugins.gadget.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FakeBarChartEntryTest {

	@Test
	public void shouldHaveInitialValue() {
		FakeBarChartEntry entry = new FakeBarChartEntry("X");

		assertThat(entry.getValue(), is(0));
	}

	@Test
	public void shouldChangeGroupField() {
		FakeBarChartEntry entry = new FakeBarChartEntry("X");
		String newGroupFieldValue = "newGroup";
		BarChartEntry realEntry = new BarChartEntry("X", newGroupFieldValue);
		entry.copyGroupFieldValue(realEntry);

		assertThat(entry.getGroupFieldValue(), equalTo(newGroupFieldValue));
	}

	@Test
	public void shouldChangeGroupFieldEvenHidden() {
		BarChartEntry entry = new FakeBarChartEntry("X");
		String newGroupFieldValue = "newGroup";
		BarChartEntry realEntry = new BarChartEntry("X", newGroupFieldValue);
		((FakeBarChartEntry) entry).copyGroupFieldValue(realEntry);

		assertThat(entry.getGroupFieldValue(), equalTo(newGroupFieldValue));
	}

	@Test
	public void shouldReturnRightInstanceOf() {
		BarChartEntry entry = new FakeBarChartEntry("X");

		assertThat(entry instanceof FakeBarChartEntry, is(true));
	}

	@Test
	public void shouldEqualEquivalentEntry() {
		BarChartEntry realEntry = new FakeBarChartEntry("X", "G");
		BarChartEntry sameEntry = new FakeBarChartEntry("X", "G");

		assertThat(sameEntry, equalTo(realEntry));
	}

	@Test
	public void shouldNotEqualRealEntry() {
		BarChartEntry realEntry = new BarChartEntry("X", "G");
		BarChartEntry fakeEntry = new FakeBarChartEntry("X", "G");

		assertThat(fakeEntry, not(equalTo(realEntry)));
		assertThat(realEntry, not(equalTo(fakeEntry)));
	}

}
