package com.tngtech.jira.plugins.gadget.configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.tngtech.jira.plugins.gadget.rest.FieldList;
import com.tngtech.jira.plugins.utils.JiraUtils;
import com.tngtech.jira.plugins.utils.Utils;
import com.tngtech.jira.plugins.utils.fields.CommonField;
import org.slf4j.LoggerFactory;

@Path("/BarChartConfiguration")
@Produces( { MediaType.APPLICATION_JSON })
public class BarChartGadgetConfiguration {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(BarChartGadgetConfiguration.class);

	private final JiraUtils jiraUtils;

	public BarChartGadgetConfiguration(JiraUtils jiraUtils) {
		this.jiraUtils = jiraUtils;
	}

	@GET
	@Path("/LookupCustomAndCommonFields")
	public Response lookupCustomAndCommonFieldNames() {
		return lookupCustomAndCommonFieldNames(true);
	}

	@GET
	@Path("/LookupCustomAndCommonFieldsWithoutNothing")
	public Response lookupCustomAndCommonFieldNamesWithoutNothing() {
		return lookupCustomAndCommonFieldNames(false);
	}

	private Response lookupCustomAndCommonFieldNames(boolean withNothing) {
		try {
		//	System.out.println("Boolean Variable With Nothing : "+withNothing);
			FieldList fieldList = getFieldList(getCustomAndCommonFieldInfos(withNothing));
		//	System.out.println("Field List : "+  fieldList.fieldNames);
			return generateResponse(fieldList);
		} catch (RuntimeException e) {
			log.error("Error while looking up custom and common field names"
					+ " for bar chart configuration", e);
			return Response.serverError().build();
		}
	}

	@GET
	@Path("/LookupCustomFields") // TODO this seems unused (unless constructed via string-concat..)
	public Response lookupCustomFieldNames() {
		try {
			FieldList fieldList = getFieldList(getCustomFieldInfos());
			return generateResponse(fieldList);
		} catch (RuntimeException e) {
			log.error("Error while looking up custom field names for bar chart configuration!", e);
			return Response.serverError().build();
		}
	}

	private Response generateResponse(FieldList fieldList) {
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		return Response.ok(fieldList).cacheControl(cacheControl).build();
	}

	private FieldList getFieldList(Map<Long, String> fieldInfos) {
		return new FieldList(fieldInfos);
	}

	private Map<Long, String> getCustomFieldInfos() {
		Map<Long, String> fieldInfos = new LinkedHashMap<Long, String>();
		enrichMapWithCustomFields(fieldInfos);
		return Utils.sortMapByValue(fieldInfos);
	}

	private Map<Long, String> getCustomAndCommonFieldInfos(boolean withNothing) {
		Map<Long, String> fieldInfos = getCommonFields(withNothing);
		enrichMapWithCustomFields(fieldInfos);
		return Utils.sortMapByValue(fieldInfos);
	}

	private Map<Long, String> getCommonFields(boolean withNothing) {
		Map<Long, String> fieldInfos = new LinkedHashMap<Long, String>();
		CommonField[] commonFields = CommonField.values();
		for (CommonField commonField : commonFields) {
			System.out.println("Common Field : "+commonField);
			System.out.println("Common Field : "+commonField.getId());
			System.out.println("Get Label Common Field : "+ jiraUtils.getLabel(commonField));

			if (withNothing || commonField.getId() != -1) {
				fieldInfos.put(Long.valueOf(commonField.getId()), jiraUtils.getLabel(commonField));
			}
		}
		return fieldInfos;
	}

	public List<CustomField> getCustomFields() {
		List<CustomField> customFields = jiraUtils.getAllCustomFields();
		if (customFields == null) {
			return new ArrayList<CustomField>(0);
		}
		return customFields;
	}

	private void enrichMapWithCustomFields(Map<Long, String> fieldInfos) {
		List<CustomField> customFields = getCustomFields();
		if (customFields != null) {
			for (CustomField field : customFields) {
				if (jiraUtils.isValidType(field.getCustomFieldType().getKey())) {
					fieldInfos.put(field.getIdAsLong(), field.getName());
				}
			}
		}
	}
}