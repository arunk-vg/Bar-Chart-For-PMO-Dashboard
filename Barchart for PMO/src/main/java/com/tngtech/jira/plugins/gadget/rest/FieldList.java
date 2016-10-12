package com.tngtech.jira.plugins.gadget.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FieldList {

	public static final String LABEL = "label";
	public static final String VALUE = "value";

	@XmlElement
	public List<Map<String, String>> fieldNames;

	@SuppressWarnings("unused")
	public FieldList() {
	}

	public FieldList(Map<Long, String> fieldInfos) {
		this.fieldNames = new ArrayList<Map<String, String>>();
		for (Map.Entry<Long, String> info : fieldInfos.entrySet()) {
			Map<String, String> entry = new HashMap<String, String>();
			entry.put(VALUE, info.getKey().toString());
			entry.put(LABEL, info.getValue());
			this.fieldNames.add(entry);
		}
	}
}