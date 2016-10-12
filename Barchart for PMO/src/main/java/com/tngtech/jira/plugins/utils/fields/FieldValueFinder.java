package com.tngtech.jira.plugins.utils.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.tngtech.jira.plugins.utils.JiraUtils;
import com.tngtech.jira.plugins.utils.Utils;

import org.slf4j.LoggerFactory;

//TODO consider breaking up class. especially test class is way to big
public class FieldValueFinder {

	public static final String OPTIONS_SEPERATOR = ", ";
	public static final String DEFAULT_FIELD_VALUE = "n.a.";
	public static final List<String> DEFAULT_GROUP_VALUE_LIST = Arrays.asList(DEFAULT_FIELD_VALUE);
	public static final List<String> DEFAULT_XAXIS_VALUE_LIST = Arrays.asList(DEFAULT_FIELD_VALUE);

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(FieldValueFinder.class);

	private JiraUtils jiraUtils;

	private boolean lastFieldWasNone = false;
	private String lastFieldName = "";

	private CustomField xAxisField = null;
	private CustomField groupField = null;

	public FieldValueFinder(JiraUtils jiraUtils) {
		init(jiraUtils);
	}

	// For Unit Tests
	public FieldValueFinder(JiraUtils jiraUtils, CustomField xAxisField, CustomField groupField) {
		init(jiraUtils);
		this.xAxisField = xAxisField;
		this.groupField = groupField;
	}

	private void init(JiraUtils jiraUtils) {
		this.jiraUtils = jiraUtils;
	}

	public List<String> findXAxisFieldValues(Issue issue, String xAxisFieldIdString) {
		if (xAxisFieldIdString == null || xAxisFieldIdString.isEmpty())
			return DEFAULT_XAXIS_VALUE_LIST;
		Long xAxisFieldId = Long.valueOf(xAxisFieldIdString);
		if (xAxisFieldId >= 0) {
			setXAxisFieldIfNeeded(xAxisFieldId);
			if (xAxisField == null)
				return DEFAULT_XAXIS_VALUE_LIST;
			return findCustomFieldValues(issue, xAxisField, DEFAULT_XAXIS_VALUE_LIST);
		} else {
			CommonField commonField = CommonField.getCommonFieldById(xAxisFieldId.intValue());
			return findCommonFieldValues(issue, commonField);
		}
	}

	public List<String> findGroupFieldValues(Issue issue, String groupFieldIdString) {
		if (groupFieldIdString == null || groupFieldIdString.isEmpty())
			return DEFAULT_GROUP_VALUE_LIST;
		Long groupFieldId = Long.valueOf(groupFieldIdString);
		if (groupFieldId >= 0) {
			setGroupFieldIfNeeded(groupFieldId);
			if (groupField == null)
				return DEFAULT_GROUP_VALUE_LIST;
			return findCustomFieldValues(issue, groupField, DEFAULT_GROUP_VALUE_LIST);
		} else {
			CommonField commonField = CommonField.getCommonFieldById(groupFieldId.intValue());
			return findCommonFieldValues(issue, commonField);
		}
	}

	public void setXAxisFieldIfNeeded(Long xAxisFieldId) {
		if (xAxisField == null && xAxisFieldId != null)
			xAxisField = new CustomFieldWrapper(jiraUtils, xAxisFieldId).getCustomField();
	}

	public void setGroupFieldIfNeeded(Long groupFieldId) {
		if (groupField == null && groupFieldId != null)
			groupField = new CustomFieldWrapper(jiraUtils, groupFieldId).getCustomField();
	}

	@SuppressWarnings("rawtypes")
	public List<String> findCustomFieldValues(Issue issue, CustomField customField, List<String> defaultReturn) {
		if (customField == null) {
			log.error("Could not find custom field for value extraction");
			return defaultReturn;
		}
		setLastMembers(customField);
		Object value = customField.getValue(issue);
		if (value == null)
			return defaultReturn;
		// Check if known case
		if (value instanceof Collection)
			return Utils.toStringList((Collection) value);
		if (value instanceof CustomFieldParams) {
			String combinedValues = combineValues(((CustomFieldParams) value).getAllValues());
			return ImmutableList.of(combinedValues);
		}
		if (value instanceof User)
			return ImmutableList.of(((User) value).getName());
		if (value instanceof Project)
			return ImmutableList.of(((Project) value).getName());
		if (value instanceof Version)
			return ImmutableList.of(((Version) value).getName());
		if (value instanceof GenericValue)
			return ImmutableList.of(((GenericValue) value).get("name").toString());
		// Default behaviour
		String valueAsString = value.toString();
		if (valueAsString.isEmpty())
			return defaultReturn;
		return ImmutableList.of(valueAsString);
	}

	@SuppressWarnings("rawtypes")
	private String combineValues(Collection allValues) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Object o : allValues) {
			builder.append(o);
			if (i < allValues.size() - 1)
				builder.append(OPTIONS_SEPERATOR);
			i++;
		}
		return builder.toString();
	}

	public List<String> findCommonFieldValues(Issue issue, CommonField groupField) {
		if (groupField == null) {
			log.error("Could not find common field for value extraction");
			return DEFAULT_GROUP_VALUE_LIST;
		}
		setLastMembers(groupField);
		List<String> values = groupField.getValues(issue);
		if (values == null || values.size() == 0 || (values.size() == 1 && values.get(0).isEmpty()))
			return DEFAULT_GROUP_VALUE_LIST;
		return values;
	}

	private void setLastMembers(CustomField customField) {
		lastFieldWasNone = false;
		lastFieldName = customField.getName();
		if (lastFieldName == null)
			lastFieldName = "";
	}

	private void setLastMembers(CommonField commonField) {
		lastFieldWasNone = commonField.equals(CommonField.None);
		lastFieldName = jiraUtils.getLabel(commonField);
		if (lastFieldName == null)
			lastFieldName = "";
	}

	public boolean getLastFieldWasNone() {
		return lastFieldWasNone;
	}

	public String getLastFieldName() {
		return lastFieldName;
	}

	public List<String> getPossibleXAxisValues(String xAxisFieldIdString) {
		System.out.println("From GetPossibleXaxisValues XAxisFieldIdString : "+xAxisFieldIdString+" xAxisField : "+xAxisField + "Default Xaxis list :"+DEFAULT_FIELD_VALUE);
		return getPossibleCustomFieldValues(xAxisFieldIdString, xAxisField, DEFAULT_XAXIS_VALUE_LIST);
	}

	public List<String> getPossibleGroupFieldValues(String groupFieldIdString) {
		return getPossibleCustomFieldValues(groupFieldIdString, groupField, DEFAULT_GROUP_VALUE_LIST);
	}

	public List<String> getPossibleCustomFieldValues(String fieldIdString, CustomField customField,
			List<String> defaultReturn) {
		if (fieldIdString == null || fieldIdString.isEmpty())
			return defaultReturn;
		List<Option> options = jiraUtils.getAllCustomFieldOptions();
		System.out.println("Get Possible Custom Field Values Options : "+options);
		Long fieldId = Long.valueOf(fieldIdString);
		setXAxisFieldIfNeeded(fieldId);
		if (options == null || customField == null)
			return defaultReturn;
		return getMatchingOptionsAndCombinations(options, customField);
	}

	public void removeImage(int index, List<Option> options) {
	     if(options != null) {
	        try {
	        	options.remove(index);
	        } catch (UnsupportedOperationException uoe) {
	        	options = new ArrayList<Option>(options);
	        	options.remove(index);
	    		System.out.println("Options removed");

	        	
	        }
	     }
	}	
	private List<String> getMatchingOptionsAndCombinations(List<Option> options, CustomField customField) {
		List<String> possibleValues = new LinkedList<String>();
	for(int i = 0; i <= options.size()-1;i ++)//Option opt : options)
	{
		String optio = options.get(i).getValue().toLowerCase();
		System.out.println(optio);
		String test = "Veriyed";
		if(optio.equals(test.toLowerCase())){
			removeImage(i,options);
			//options.remove(i); 
			System.out.println("Options SIZE ::::::::::::::::::::::" + options.size());
			System.out.println("////////////////////////////////////////////////////"
					+ "////////////////////////////////Options Removed//////////////////////////////////////////////////"
					+ "/////////////////////////////////"
					+ "////////////////////////////////");
		}		
	}
	
		/*System.out.println(options);
		
		for (Option optionSOP : options)
		{
			//test = 	optionSOP.getRelatedCustomField().getCustomField();
			//String ValueofOption = test.getFieldName();//.getValue();
			System.out.println("OPTION ID : "+ optionSOP.getOptionId() + " OPTION VALUE"+optionSOP.getValue()+" Veriyed value"+optionSOP.equals("Veriyed"));// +"OptionSOP getName : "+ test.getName()+ "ValueofOption getFieldNamevalue : "+ ValueofOption +" CustomField Name/value : "+customField.toString());
		}*/
	try
	{

		for (Option option : options)
	//		test = 	option.getRelatedCustomField().getCustomField().equals(customField);
	//	test.getName();	
		{
		//	if(option.equals("Red") || option.equals("Yellow") || option.equals("Green"))
		//	{
			System.out.println("Options SIZE ::::::::::::::::::::::" + options.size()+ " Options get Custom field that throws error :::::::::::::::::::::::: "+option.getRelatedCustomField().getName());

			if (option.getRelatedCustomField().getCustomField().equals(customField) && option.getParentOption() == null) 
			{
				System.out.println("LOOP customfield : "+customField + " Options Size "+options.size()+"");
				List<Option> children = option.getChildOptions();
				possibleValues.add(option.getValue());
				if (children != null)
					for (Option child : children)
						possibleValues.add(option.getValue() + OPTIONS_SEPERATOR + child.getValue());
			}
			}
		//}
	}
	catch(DataAccessException da)
	{
		System.out.println("DAE Exception Message : "+da.getMessage()+ " DAE Localized Message : "+da.getLocalizedMessage() + 
				" DAE GET Cause : "+da.getCause()+ " DAE Get STACK trace :  "+da.getStackTrace());
	}
		return possibleValues;
	}

}
