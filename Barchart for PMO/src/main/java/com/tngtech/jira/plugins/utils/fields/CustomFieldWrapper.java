package com.tngtech.jira.plugins.utils.fields;

import com.atlassian.jira.issue.fields.CustomField;
import com.tngtech.jira.plugins.utils.JiraUtils;
import org.slf4j.LoggerFactory;

public class CustomFieldWrapper {

	private JiraUtils jiraUtils;

	private String name = null;
	private Long id = null;
	private CustomField customField;

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(CustomFieldWrapper.class);

	public CustomFieldWrapper(JiraUtils jiraUtils, String name) {
		this.jiraUtils = jiraUtils;
		this.name = name;
		getCustomFieldAndId();
	}

	public CustomFieldWrapper(JiraUtils jiraUtils, Long id) {
		this.jiraUtils = jiraUtils;
		this.id = id;
		getCustomFieldAndName();
	}

	public CustomField getCustomField() {
		if (customField == null) {
			if (id == null) {
				getCustomFieldAndId();
			} else {
				getCustomFieldAndName();
			}
		}
		return customField;
	}

	private void getCustomFieldAndId() {
		customField = jiraUtils.getCustomFieldByName(name);
		if (customField != null) {
			this.id = customField.getIdAsLong();
		} else {
			log.debug("Could not retrieve id from non existant custom field!");
		}
	}

	private void getCustomFieldAndName() {
		customField = jiraUtils.getCustomFieldById(id);
		if (customField != null) {
			this.name = customField.getName();
		} else {
			log.debug("Could not retrieve name from non existant custom field!");
		}
	}

}
