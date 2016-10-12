package com.tngtech.jira.plugins.utils.fields;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.tngtech.jira.plugins.utils.JiraUtils;
import com.tngtech.jira.plugins.utils.Utils;

public class FieldValueFinderTest {

	private FieldValueFinder finder;
	@Before
	public void setup() {
		JiraUtils jiraUtils = mock(JiraUtils.class);
		when(jiraUtils.getLabel(any(CommonField.class))).thenReturn("name");
		finder = new FieldValueFinder(jiraUtils);
	}

	@Test
	public void shouldHaveAppropriateDefaultValue() {
		String defaultValue = FieldValueFinder.DEFAULT_FIELD_VALUE;
		assertThat(defaultValue, notNullValue());
		assertThat(defaultValue.isEmpty(), is(false));

		List<String> defaultGroupValueList = FieldValueFinder.DEFAULT_GROUP_VALUE_LIST;
		assertThat(defaultGroupValueList, notNullValue());

		List<String> defaultXAxisValueList = FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST;
		assertThat(defaultXAxisValueList, notNullValue());
	}

	// Ignore error logging from src code. Tests create error cases on purpose and check their handling!
	@Test
	public void shouldHandleNullFields() {
		Issue issue = mock(Issue.class);

		List<String> values = finder.findCommonFieldValues(issue, null);
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));

		values = finder.findCustomFieldValues(issue, null, FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldHandleNullIssues() {
		CommonField commonField = mock(CommonField.class);
		CustomField customField = mock(CustomField.class);

		List<String> values = finder.findCommonFieldValues(null, commonField);
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));

		values = finder.findCustomFieldValues(null, customField, FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldReturnDefaultValueListWhenNullCommonFieldNames() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		when(commonField.getValues(issue)).thenReturn(null);

		List<String> foundGroupValue = finder.findCommonFieldValues(issue, commonField);

		assertThat(foundGroupValue, notNullValue());
		assertThat(foundGroupValue, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldReturnDefaultValueListWhenEmptyCommonFieldNamesList() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		List<String> groupValues = new ArrayList<String>(0);
		when(commonField.getValues(issue)).thenReturn(groupValues);

		List<String> foundGroupValue = finder.findCommonFieldValues(issue, commonField);

		assertThat(foundGroupValue, notNullValue());
		assertThat(foundGroupValue, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldReturnDefaultValueListWhenEmptyCommonFieldName() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		List<String> groupValues = ImmutableList.of("");
		when(commonField.getValues(issue)).thenReturn(groupValues);

		List<String> foundGroupValue = finder.findCommonFieldValues(issue, commonField);

		assertThat(foundGroupValue, notNullValue());
		assertThat(foundGroupValue, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldReturnCommonFieldValue() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		List<String> groupValues = ImmutableList.of("group");
		when(commonField.getValues(issue)).thenReturn(groupValues);

		List<String> foundGroupValue = finder.findCommonFieldValues(issue, commonField);

		assertThat(foundGroupValue, notNullValue());
		assertThat(foundGroupValue, equalTo(groupValues));
	}

	@Test
	public void shouldReturnAllCommonFieldValues() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		List<String> groupValues = Arrays.asList("group1", "group2");
		when(commonField.getValues(issue)).thenReturn(groupValues);

		List<String> foundGroupValue = finder.findCommonFieldValues(issue, commonField);

		assertThat(foundGroupValue, notNullValue());
		assertThat(foundGroupValue, equalTo(groupValues));
	}

	@Test
	public void shouldReturnDefaultCustomFieldValueWhenNullNames() {
		Issue issue = mock(Issue.class);
		CustomField customField = mock(CustomField.class);
		when(customField.getValue(issue)).thenReturn(null);

		List<String> foundGroupValues = finder.findCustomFieldValues(issue, customField,
				FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);

		assertThat(foundGroupValues, notNullValue());
		assertThat(foundGroupValues, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldReturnCustomFieldValue() {
		Issue issue = mock(Issue.class);
		CustomField customField = mock(CustomField.class);
		String groupValue = "group";
		when(customField.getValue(issue)).thenReturn(groupValue);

		List<String> foundGroupValues = finder.findCustomFieldValues(issue, customField,
				FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);

		assertThat(foundGroupValues, notNullValue());
		assertThat(foundGroupValues, equalTo((List<String>)ImmutableList.of(groupValue)));
	}

	@Test
	public void shouldReturnCustomFieldValuesList() {
		Issue issue = mock(Issue.class);
		CustomField customField = mock(CustomField.class);
		List<String> groupValues = Arrays.asList("group1", "group2");
		when(customField.getValue(issue)).thenReturn(groupValues);

		List<String> foundGroupValues = finder.findCustomFieldValues(issue, customField,
				FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);

		assertThat(foundGroupValues, notNullValue());
		assertThat(foundGroupValues, equalTo(groupValues));
	}

	@Test
	public void shouldReturnCustomFieldValuesFromCascadingSelect() {
		Issue issue = mock(Issue.class);
		CustomField customField = mock(CustomField.class);
		CustomFieldParams params = mock(CustomFieldParams.class);
		List<String> groupValues = Arrays.asList("group1", "group2");
		when(params.getAllValues()).thenReturn(groupValues);
		when(customField.getValue(issue)).thenReturn(params);

		List<String> foundGroupValues = finder.findCustomFieldValues(issue, customField,
				FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);
		String joinedValue = "group1" + FieldValueFinder.OPTIONS_SEPERATOR + "group2";

		assertThat(foundGroupValues, notNullValue());
		assertThat(foundGroupValues, equalTo((List<String>)ImmutableList.of(joinedValue)));
	}

	@Test
	public void xAxisFieldValueLookupShouldHandleNullAndEmptyId() {
		Issue issue = mock(Issue.class);

		List<String> values = finder.findXAxisFieldValues(issue, null);
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST));

		values = finder.findXAxisFieldValues(issue, "");
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST));
	}

	@Test
	public void groupFieldValueLookupShouldHandleNullAndEmptyId() {
		Issue issue = mock(Issue.class);

		List<String> values = finder.findGroupFieldValues(issue, null);
		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
		values = finder.findGroupFieldValues(issue, "");

		assertThat(values, notNullValue());
		assertThat(values, equalTo(FieldValueFinder.DEFAULT_GROUP_VALUE_LIST));
	}

	@Test
	public void shouldSaveAndReturnLastCommonFieldLookupName() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		when(commonField.getValues(issue)).thenReturn(ImmutableList.of("group"));
		String name = "name";
		when(commonField.getKey()).thenReturn(name);

		finder.findCommonFieldValues(issue, commonField);
		String foundName = finder.getLastFieldName();

		assertThat(foundName, notNullValue());
		assertThat(foundName, equalTo(name));
	}

	@Test
	public void shouldSaveAndReturnLastCommonFieldLookupIsNone() {
		Issue issue = mock(Issue.class);
		CommonField commonField = mock(CommonField.class);
		when(commonField.getValues(issue)).thenReturn(ImmutableList.of("group"));
		when(commonField.getId()).thenReturn(CommonField.None.getId());

		finder.findCommonFieldValues(issue, commonField);
		boolean foundName = finder.getLastFieldWasNone();

		assertThat(foundName, is(false));
	}

	@Test
	public void shouldSaveAndReturnLastCustomFieldLookupName() {
		Issue issue = mock(Issue.class);
		CustomField customField = mock(CustomField.class);
		when(customField.getValue(issue)).thenReturn("group");
		String name = "name";
		when(customField.getName()).thenReturn(name);
		finder.findCustomFieldValues(issue, customField, FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);

		String foundName = finder.getLastFieldName();

		assertThat(foundName, notNullValue());
		assertThat(foundName, equalTo(name));
	}

	@Test
	public void shouldSaveAndReturnLastCustomFieldLookupIsNone() {
		Issue issue = mock(Issue.class);
		CustomField customField = mock(CustomField.class);
		when(customField.getValue(issue)).thenReturn("group");

		finder.findCustomFieldValues(issue, customField, FieldValueFinder.DEFAULT_GROUP_VALUE_LIST);
		boolean foundName = finder.getLastFieldWasNone();

		assertThat(foundName, is(false));
	}

	@Test
	public void shouldReturnOnlyDefaultPossibleXValuesWithoutId() {
		List<String> foundValues = finder.getPossibleXAxisValues(null);
		assertThat(foundValues, notNullValue());
		assertThat(foundValues, equalTo(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST));

		foundValues = finder.getPossibleXAxisValues("");
		assertThat(foundValues, notNullValue());
		assertThat(foundValues, equalTo(FieldValueFinder.DEFAULT_XAXIS_VALUE_LIST));
	}

	@Test
	public void shouldReturnPossibleXAxisValuesOfRightCustomField() {
		JiraUtils jiraUtils = mock(JiraUtils.class);
		CustomField xAxisField = mock(CustomField.class);
		CustomField otherCustomField = mock(CustomField.class);
		List<Option> allOptions = new ArrayList<Option>();
		Option option1 = mockOption(xAxisField, "1st");
		allOptions.add(option1);
		Option option2 = mockOption(otherCustomField, "2nd");
		allOptions.add(option2);
		Option option3 = mockOption(xAxisField, "3rd");
		allOptions.add(option3);
		when(jiraUtils.getAllCustomFieldOptions()).thenReturn(allOptions);

		finder = new FieldValueFinder(jiraUtils, xAxisField, null);
		List<String> foundValues = finder.getPossibleXAxisValues("5");

		assertThat(foundValues.size(), is(2));
		assertThat(foundValues.get(0), equalTo("1st"));
		assertThat(foundValues.get(1), equalTo("3rd"));
	}

	@Test
	public void shouldReturnPossibleCombinationsOfXAxisValues() {
		JiraUtils jiraUtils = mock(JiraUtils.class);
		CustomField xAxisField = mock(CustomField.class);
		List<Option> allOptions = new ArrayList<Option>();
		Option option1 = mockOption(xAxisField, "1st");
		Option option2 = mockOption(xAxisField, "2nd", option1);
		Option option3 = mockOption(xAxisField, "3rd", option1);
		when(option1.getChildOptions()).thenReturn(Arrays.asList(option2, option3));
		Option option4 = mockOption(xAxisField, "4th");
		allOptions.add(option1);
		allOptions.add(option2);
		allOptions.add(option3);
		allOptions.add(option4);
		when(jiraUtils.getAllCustomFieldOptions()).thenReturn(allOptions);

		finder = new FieldValueFinder(jiraUtils, xAxisField, null);
		List<String> foundValues = finder.getPossibleXAxisValues("5");

		assertThat(foundValues.size(), is(4));
		assertThat(foundValues.get(0), equalTo("1st"));
		assertThat(foundValues.get(1), equalTo("1st" + FieldValueFinder.OPTIONS_SEPERATOR + "2nd"));
		assertThat(foundValues.get(2), equalTo("1st" + FieldValueFinder.OPTIONS_SEPERATOR + "3rd"));
		assertThat(foundValues.get(3), equalTo("4th"));
	}

	private Option mockOption(CustomField relatedCustomField, String value) {
		Option option = mock(Option.class);
		FieldConfig fieldConfig = mock(FieldConfig.class);
		when(fieldConfig.getCustomField()).thenReturn(relatedCustomField);
		when(option.getRelatedCustomField()).thenReturn(fieldConfig);
		when(option.getValue()).thenReturn(value);
		return option;
	}

	private Option mockOption(CustomField relatedCustomField, String value, Option parent) {
		Option option = mockOption(relatedCustomField, value);
		when(option.getParentOption()).thenReturn(parent);
		return option;
	}

}
