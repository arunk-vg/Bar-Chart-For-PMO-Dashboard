package com.tngtech.jira.plugins.gadget.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {

	@XmlElement
	public List<String> messages = new ArrayList<String>();

	public Message(List<String> messages) {
		this.messages = messages;
	}

	@SuppressWarnings("unused")
	public Message() { }
}
