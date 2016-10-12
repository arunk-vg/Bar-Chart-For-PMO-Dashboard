package com.tngtech.jira.plugins.gadget.configuration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.google.common.collect.ImmutableList;
import com.tngtech.jira.plugins.utils.JiraUtils;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.fields.CustomField;
import com.tngtech.jira.plugins.gadget.rest.FieldList;
import com.tngtech.jira.plugins.utils.fields.CommonField;

public class BarChartGadgetConfigurationTest {

	private BarChartGadgetConfiguration configuration;
	private JiraUtils jiraUtils;

	@Before
	public void setup() {
		jiraUtils = mock(JiraUtils.class);
		for (CommonField field : CommonField.getSortedValues()) {
			String key = field.getKey();
			when(jiraUtils.getLabel(field)).thenReturn(key);
		}
		when(jiraUtils.isValidType("com.atlassian.jira.plugin.system.customfieldtypes:select")).thenReturn(true);
		configuration = new BarChartGadgetConfiguration(jiraUtils);
	}

	@Test
	public void shouldReturnOnlyCommonFields() {
		Response response = configuration.lookupCustomAndCommonFieldNames();
		List<Map<String, String>> customFieldInfos = ((FieldList) response.getEntity()).fieldNames;

		List<CustomField> customFields = Collections.emptyList();
		when(jiraUtils.getAllCustomFields()).thenReturn(customFields);

		CommonField[] commonFields = CommonField.getSortedValues();

		assertThat(customFieldInfos, notNullValue());
		assertThat(customFieldInfos.size(), is(commonFields.length));
		for (int i = 0; i < commonFields.length; i++) {
			customFieldListContains(customFieldInfos.get(i), Long.valueOf(commonFields[i].getId()), commonFields[i]
					.getKey());
		}
	}

	@Test
	public void shouldReturnCustomFieldNames() {
		List<CustomField> fields = ImmutableList.of(
				mockCustomField(1L, "First Field"),
				mockCustomField(2L, "Second Field")
		);
		when(jiraUtils.getAllCustomFields()).thenReturn(fields);

		Response response = configuration.lookupCustomAndCommonFieldNames();
		when(jiraUtils.getTranslatedText(anyString())).thenReturn("label");

		List<Map<String, String>> customFieldInfos = ((FieldList) response.getEntity()).fieldNames;
		int countCommonFields = CommonField.values().length;

		assertThat(customFieldInfos, notNullValue());
		assertThat(customFieldInfos.size(), is(countCommonFields + 2));
		customFieldListContains(customFieldInfos, 1L, "First Field");
		customFieldListContains(customFieldInfos, 2L, "Second Field");
	}

	@Test
	public void shouldReturnOnlyCustomFieldNames() {
		List<CustomField> fields = ImmutableList.of(
				mockCustomField(1L, "First Field"),
				mockCustomField(2L, "Second Field")
		);
		when(jiraUtils.getAllCustomFields()).thenReturn(fields);

		Response response = configuration.lookupCustomFieldNames();

		List<Map<String, String>> customFieldInfos = ((FieldList) response.getEntity()).fieldNames;

		assertThat(customFieldInfos, notNullValue());
		assertThat(customFieldInfos.size(), is(2));
		customFieldListContains(customFieldInfos.get(0), 1L, "First Field");
		customFieldListContains(customFieldInfos.get(1), 2L, "Second Field");
	}

	private CustomField mockCustomField(Long id, String name) {
		CustomField customField = mock(CustomField.class);
		when(customField.getIdAsLong()).thenReturn(id);
		when(customField.getName()).thenReturn(name);
		CustomFieldType type = mock(CustomFieldType.class);
		when(type.getKey()).thenReturn("com.atlassian.jira.plugin.system.customfieldtypes:select");
		when(customField.getCustomFieldType()).thenReturn(type);
		return customField;
	}

	private void customFieldListContains(Map<String, String> customFieldInfos, Long id, String value) {
		assertThat(customFieldInfos.containsKey(FieldList.LABEL), is(true));
		assertThat(customFieldInfos.get(FieldList.LABEL), is(value.toString()));
		assertThat(customFieldInfos.containsKey(FieldList.VALUE), is(true));
		assertThat(customFieldInfos.get(FieldList.VALUE), is(id.toString()));
	}

	// TODO refactor test
	private void customFieldListContains(List<Map<String, String>> customFieldInfosList, Long id, String value) {
		for (Map<String, String> customFieldInfos : customFieldInfosList) {
			if (customFieldInfos.get(FieldList.VALUE).equals(id.toString())) {
				customFieldListContains(customFieldInfos, id, value);
				return;
			}
		}
		assertThat(false, is(true));
	}

}
