package com.tngtech.jira.plugins.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

public class JodaTimeTest {

	@Test
	public void monthShouldBeOneToTwelve() {
		DateTime dateTime = new DateTime(2011, 1, 10, 0, 0, 0, 0).minusMonths(1);
		
		assertThat(dateTime.getYear(), is(2010));
		assertThat(dateTime.getMonthOfYear(), is(12));
	}

}
