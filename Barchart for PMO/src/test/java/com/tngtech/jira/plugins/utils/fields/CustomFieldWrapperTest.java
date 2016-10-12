package com.tngtech.jira.plugins.utils.fields;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.atlassian.jira.issue.fields.CustomField;
import com.tngtech.jira.plugins.utils.JiraUtils;

public class CustomFieldWrapperTest {

	@Test
	public void shouldGetCustomFieldByName() {
		String name = "custom_field_name";
		JiraUtils jiraUtils = mock(JiraUtils.class);
		CustomField customField = mock(CustomField.class);
		when(jiraUtils.getCustomFieldByName(name)).thenReturn(customField);

		CustomFieldWrapper wrapper = new CustomFieldWrapper(jiraUtils, name);

		assertThat(wrapper.getCustomField(), equalTo(customField));
	}

	@Test
	public void shouldGetCustomFieldById() {
		Long id = Long.valueOf(42);
		JiraUtils jiraUtils = mock(JiraUtils.class);
		CustomField customField = mock(CustomField.class);
		when(jiraUtils.getCustomFieldById(id)).thenReturn(customField);

		CustomFieldWrapper wrapper = new CustomFieldWrapper(jiraUtils, id);

		assertThat(wrapper.getCustomField(), equalTo(customField));
	}

}
