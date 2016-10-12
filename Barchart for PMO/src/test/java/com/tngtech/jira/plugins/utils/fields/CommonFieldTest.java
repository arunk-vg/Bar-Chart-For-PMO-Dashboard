package com.tngtech.jira.plugins.utils.fields;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CommonFieldTest {

	@Test
	public void shouldOnlyContainNegativeIds() {
		CommonField[] fields = CommonField.values();

		for (CommonField field : fields) {
			assertThat(field.getId(), lessThan(0));
		}
	}

	@Test
	public void shouldFindNoField() {
		assertThat(CommonField.getCommonFieldById(0), nullValue());
		assertThat(CommonField.getCommonFieldById(1), nullValue());
	}

	@Test
	public void shouldHaveNoDuplicateIds() {
		CommonField[] fields = CommonField.values();

		for (int i = 0; i < fields.length; i++) {
			for (int j = i + 1; j < fields.length; j++) {
				assertThat(fields[i].getId(), not(fields[j].getId()));
			}
		}
	}

	@Test
	public void shouldFindAllFields() {
		CommonField[] fields = CommonField.values();

		for (CommonField field : fields) {
			CommonField foundField = CommonField.getCommonFieldById(field.getId());
			assertThat(field.equals(foundField), is(true));
		}
	}
}
