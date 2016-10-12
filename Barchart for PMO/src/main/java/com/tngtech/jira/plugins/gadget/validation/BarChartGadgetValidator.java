package com.tngtech.jira.plugins.gadget.validation;

import com.tngtech.jira.plugins.gadget.rest.ErrorCollection;
import com.tngtech.jira.plugins.gadget.rest.ValidationError;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/BarChartValidation")
public class BarChartGadgetValidator {

	@GET
	@Path("validate")
	public Response validate(
			@QueryParam("projectOrFilterId") String projectOrFilterId,
			@QueryParam("axisField") String axisField,
			@QueryParam("groupField") String groupField,
			@QueryParam("plotWidth") String plotWidth,
			@QueryParam("showTable") String showTable,
			@QueryParam("tableFontSize") String tableFontSize
			) {
		ErrorCollection errorCollection = new ErrorCollection();
		if (StringUtils.isEmpty(projectOrFilterId)) {
			ValidationError validationError = new ValidationError(
					"projectOrFilterId",
					"gadget.barchart.config.projectOrFilterId.required"
			);
			errorCollection.addError(validationError);
		}
		if (plotWidth == null || !plotWidth.matches("\\d+")) {
			ValidationError validationError = new ValidationError(
					"plotWidth",
					"gadget.barchart.config.integer.required"
			);
			errorCollection.addError(validationError);
		}
		if (tableFontSize == null || !tableFontSize.matches("\\d+")) {
			ValidationError validationError = new ValidationError(
					"tableFontSize",
					"gadget.barchart.config.integer.required"
			);
			errorCollection.addError(validationError);
		}
		
		if (errorCollection.hasErrors()) {
			return Response.status(Response.Status.BAD_REQUEST).entity(errorCollection).build();
		}
		return Response.ok().build();
	}
}
