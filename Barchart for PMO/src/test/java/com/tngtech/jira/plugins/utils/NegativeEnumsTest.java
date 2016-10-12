package com.tngtech.jira.plugins.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NegativeEnumsTest {

	enum TestEnum {
		T1(-1), T2(-2);

		private int value;

		private TestEnum(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	@Test
	public void shouldGetNegativeIndizes() {
		assertThat(TestEnum.T1.getValue(), is(-1));
		assertThat(TestEnum.T2.getValue(), is(-2));
	}

}
