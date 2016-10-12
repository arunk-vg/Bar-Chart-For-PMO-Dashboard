package com.tngtech.jira.plugins.utils;

import java.util.List;
import java.util.Set;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.google.common.collect.ImmutableSet;
import com.tngtech.jira.plugins.utils.fields.CommonField;

// TODO this should be a service
public class JiraUtils {
	private static final Set<String> VALID_CUSTOM_FIELD_KEYS = ImmutableSet.of(
			"com.atlassian.jira.plugin.system.customfieldtypes:select",
			"com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes",
			"com.atlassian.jira.plugin.system.customfieldtypes:datetime",
			"com.atlassian.jira.plugin.system.customfieldtypes:cascadingselect",
			"com.atlassian.jira.plugin.system.customfieldtypes:textfield",
			"com.atlassian.jira.plugin.system.customfieldtypes:multiuserpicker",
			"com.atlassian.jira.plugin.system.customfieldtypes:multigrouppicker",
			"com.atlassian.jira.plugin.system.customfieldtypes:userpicker",
			"com.atlassian.jira.plugin.system.customfieldtypes:datepicker",
			"com.atlassian.jira.plugin.system.customfieldtypes:version",
			"com.atlassian.jira.plugin.system.customfieldtypes:url",
			"com.atlassian.jira.plugin.system.customfieldtypes:textarea",
			"com.atlassian.jira.plugin.system.customfieldtypes:grouppicker",
			"com.atlassian.jirafisheyeplugin:hiddenjobswitch",
			"com.atlassian.jirafisheyeplugin:jobcheckbox",
			"com.atlassian.jira.plugin.system.customfieldtypes:multiselect",
			"com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons",
			"com.atlassian.jira.plugin.system.customfieldtypes:project",
			"com.atlassian.jira.plugin.system.customfieldtypes:labels",
			"com.atlassian.jira.plugin.system.customfieldtypes:multiversion",
			"com.atlassian.jira.plugin.system.customfieldtypes:float"
	);

	private final JiraAuthenticationContext authenticationContext;
	private final SearchService searchService;
	private final SearchRequestService searchRequestService;

	public JiraUtils(final SearchService searchService, final JiraAuthenticationContext authenticationContext,
			final SearchRequestService searchRequestService) {
		this.authenticationContext = authenticationContext;
		this.searchService = searchService;
		this.searchRequestService = searchRequestService;
	}

	public User getUser() {
		ApplicationUser user = getApplicationUser();
		User directoryUser = null;
		if (user != null) {
			directoryUser = user.getDirectoryUser();
		}
		return directoryUser;
	}

	private ApplicationUser getApplicationUser() {
		return authenticationContext.getUser();
	}

	public SearchRequest getSearchRequestForFilter(Long filterId) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(getApplicationUser());
		return searchRequestService.getFilter(jiraServiceContext, filterId);
	}

	@SuppressWarnings("rawtypes")
	public SearchResults getSearchResults(Query query, PagerFilter pagerFilter) throws SearchException {
		return searchService.search(getUser(), query, pagerFilter);
	}

	public Project getProjectById(Long projectId) {
		ProjectManager projectManager = ComponentManager.getInstance().getProjectManager();
		PermissionManager permissionManager = ComponentManager.getInstance().getPermissionManager();
		Project project = projectManager.getProjectObj(projectId);
		if (permissionManager.hasPermission(ProjectPermissions.BROWSE_PROJECTS, project, getApplicationUser())) {
			return project;
		} else {
			return null;
		}
	}

	public CustomField getCustomFieldById(Long fieldId) {
		CustomFieldManager customFieldManager =	ComponentAccessor.getCustomFieldManager();
		//CustomFieldManager customFieldManager = ComponentManager.getInstance().getCustomFieldManager();
		//System.out.println("fieldId : "+fieldId);
		return customFieldManager.getCustomFieldObject(fieldId);
	}

	public CustomField getCustomFieldByName(String fieldName) {
		//CustomFieldManager customFieldManager = ComponentManager.getInstance().getCustomFieldManager();
		CustomFieldManager customFieldManager =	ComponentAccessor.getCustomFieldManager();
	//	System.out.println("fieldName : "+fieldName);

		return customFieldManager.getCustomFieldObjectByName(fieldName);
	}

	public List<Option> getAllCustomFieldOptions() {
		OptionsManager manager = ComponentAccessor.getOptionsManager();//ComponentManager.getComponent(OptionsManager.class);
		return manager.getAllOptions();
	}
	
	public List<Option> getSpecificCustomFieldOptions() {
		OptionsManager manager = ComponentAccessor.getOptionsManager();//ComponentManager.getComponent(OptionsManager.class);
		return manager.getAllOptions();
	}

	public List<CustomField> getAllCustomFields() {
		CustomFieldManager customFieldManager = ComponentManager.getInstance().getCustomFieldManager();
		return customFieldManager.getCustomFieldObjects();
	}

	public I18nBean geti18nBean() {
		return new I18nBean(getUser());
	}

	public String getTranslatedText(String key) {
		return authenticationContext.getI18nHelper().getText(key);
	}

	public boolean isValidType(String customFieldType) {
		return VALID_CUSTOM_FIELD_KEYS.contains(customFieldType);
	}

	public String getLabel(CommonField field) {
		return getTranslatedText(field.getKey());
	}
}
