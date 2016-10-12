package com.tngtech.jira.plugins.utils;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RegExpTest {

	@Test
	public void shouldReplaceNonDigits() {
		String input = "bla blup-_?123asd45!\"";
		String output = input.replaceAll("[^\\d]", "");

		assertThat(output, equalTo("12345"));
	}

}
