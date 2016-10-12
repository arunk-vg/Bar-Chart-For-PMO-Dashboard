package com.tngtech.jira.plugins.utils.fields;

import java.util.*;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.google.common.collect.ImmutableList;
import com.tngtech.jira.plugins.utils.Utils;

public enum CommonField {

	None(-1, "gadget.barchart.commonfields.nothing") {
		@Override
		public List<String> getValues(Issue issue) {
			return new ArrayList<String>(0);
		}
	},

	Assignee(-2, "gadget.barchart.commonfields.assignee") {
		@Override
		public List<String> getValues(Issue issue) {
			User assigneeUser = issue.getAssigneeUser();
			if (assigneeUser == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(assigneeUser.getName());
		}
	},
	Components(-3, "gadget.barchart.commonfields.components") {
		@Override
		public List<String> getValues(Issue issue) {
			Collection<ProjectComponent> components = issue.getComponentObjects();
			if (components == null) {
				return new ArrayList<String>(0);
			}
			List<String> componentssStrings = new ArrayList<String>(components.size());
			for (ProjectComponent component : components) {
				componentssStrings.add(component.getName());
			}
			return componentssStrings;
		}
	},
	IssueType(-4, "gadget.barchart.commonfields.issuetype") {
		@Override
		public List<String> getValues(Issue issue) {
			IssueType issueType = issue.getIssueTypeObject();
			if (issueType == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(issueType.getName());
		}
	},
	FixVersionsNonArchived(-5, "gadget.barchart.commonfields.fixversion.nonarchived") {
		@Override
		public List<String> getValues(Issue issue) {
			Collection<Version> versions = issue.getFixVersions();
			if (versions == null) {
				return new ArrayList<String>(0);
			}
			List<String> versionsStrings = new ArrayList<String>(versions.size());
			for (Version version : versions) {
				if (!version.isArchived()) {
					versionsStrings.add(version.getName());
				}
			}
			return versionsStrings;
		}
	},
	FixVersionsAll(-6, "gadget.barchart.commonfields.fixversion.all") {
		@Override
		public List<String> getValues(Issue issue) {
			Collection<Version> versions = issue.getFixVersions();
			if (versions == null) {
				return new ArrayList<String>(0);
			}
			List<String> versionsStrings = new ArrayList<String>(versions.size());
			for (Version version : versions) {
				versionsStrings.add(version.getName());
			}
			return versionsStrings;
		}
	},
	Priority(-7, "gadget.barchart.commonfields.priority") {
		@Override
		public List<String> getValues(Issue issue) {
			Priority priority = issue.getPriorityObject();
			if (priority == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(priority.getName());
		}
	},
	Project(-8, "gadget.barchart.commonfields.project") {
		@Override
		public List<String> getValues(Issue issue) {
			Project project = issue.getProjectObject();
			if (project == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(project.getName());
		}
	},
	RaisedInVersionsNonArchived(-9, "gadget.barchart.commonfields.raisedversion.nonarchived") {
		@Override
		public List<String> getValues(Issue issue) {
			Collection<Version> versions = issue.getAffectedVersions();
			if (versions == null) {
				return new ArrayList<String>(0);
			}
			List<String> versionsStrings = new ArrayList<String>(versions.size());
			for (Version version : versions) {
				if (!version.isArchived()) {
					versionsStrings.add(version.getName());
				}
			}
			return versionsStrings;
		}
	},
	RaisedInVersionsAll(-10, "gadget.barchart.commonfields.raisedversion.all") {
		@Override
		public List<String> getValues(Issue issue) {
			Collection<Version> versions = issue.getAffectedVersions();
			if (versions == null) {
				return new ArrayList<String>(0);
			}
			List<String> versionsStrings = new ArrayList<String>(versions.size());
			for (Version version : versions) {
				versionsStrings.add(version.getName());
			}
			return versionsStrings;
		}
	},
	Reporter(-11, "gadget.barchart.commonfields.reporter") {
		@Override
		public List<String> getValues(Issue issue) {
			User reporterUser = issue.getReporterUser();
			if (reporterUser == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(reporterUser.getName());
		}
	},
	Resolution(-12, "gadget.barchart.commonfields.resolution") {
		@Override
		public List<String> getValues(Issue issue) {
			Resolution resolution = issue.getResolutionObject();
			if (resolution == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(resolution.getName());
		}
	},
	Status(-13, "gadget.barchart.commonfields.status") {
		@Override
		public List<String> getValues(Issue issue) {
			Status status = issue.getStatusObject();
			if (status == null) {
				return new ArrayList<String>(0);
			}
			return ImmutableList.of(status.getName());
		}
	},
	Labels(-14, "gadget.barchart.commonfields.labels") {
		@Override
		public List<String> getValues(Issue issue) {
			Set<Label> labels = issue.getLabels();
			if (labels == null) {
				return new ArrayList<String>(0);
			}
			List<String> labelsStrings = new ArrayList<String>(labels.size());
			for (Label label : labels) {
				labelsStrings.add(label.getLabel());
			}
			return labelsStrings;
		}
	};

	private int id;
	private String labelKey;

	private CommonField(int id, String labelKey) {
		this.id = id;
		this.labelKey = labelKey;
	}

	public static CommonField getCommonFieldById(int id) {
		CommonField[] fields = CommonField.values();
		for (CommonField field : fields) {
			if (field.getId() == id) {
				return field;
			}
		}
		return null;
	}

	public int getId() {
		return id;
	}

	public String getKey() {
		return labelKey;
	}

	public abstract List<String> getValues(Issue issue);

	public static CommonField[] getSortedValues() {
		CommonField[] unsortedFields = CommonField.values();
		int size = unsortedFields.length;
		List<CommonField> sortedFields = new ArrayList<CommonField>(size);
		for (int i = 0; i < size; i++) {
			CommonField current = null;
			for (CommonField field : unsortedFields) {
				if ((current == null || current.getKey().compareTo(field.getKey()) > 0)
						&& !sortedFields.contains(field)) {
					current = field;
				}
			}
			sortedFields.add(current);
		}
		return sortedFields.toArray(new CommonField[size]);
	}

}
