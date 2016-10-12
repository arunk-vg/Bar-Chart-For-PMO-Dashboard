package com.tngtech.jira.plugins.utils;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EnumEqualsTest {

	enum TestEnum {
		T1(1), T2(1);

		public int value;

		private TestEnum(int value) {
			this.value = value;
		}
	}

	@Test
	public void shouldBeEqual() {
		TestEnum enum1 = TestEnum.T1;
		TestEnum enum2 = TestEnum.T1;

		assertThat(enum1.equals(enum2), is(true));
		assertThat(enum1.hashCode(), is(enum2.hashCode()));
	}

	@Test
	public void shouldNotBeEqualEvenWithSameValue() {
		TestEnum enum1 = TestEnum.T1;
		TestEnum enum2 = TestEnum.T2;

		assertThat(enum1.equals(enum2), is(false));
		assertThat(enum1.hashCode(), not(enum2.hashCode()));
	}

}
