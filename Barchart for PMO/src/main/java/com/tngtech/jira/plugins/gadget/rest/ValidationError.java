package com.tngtech.jira.plugins.gadget.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class ValidationError
{
    public ValidationError() {

    }

    public void setField(String field) {
        this.field = field;
    }

    public void setParams(Collection<String> params) {
        this.params = params;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ValidationError(String field, String error, Collection<String> params) {
        this.field = field;
        this.error = error;
        this.params = params;
    }

    @XmlElement
    private String field;

    public ValidationError(String field, String error) {
        this.field = field;
        this.error = error;
    }

    @XmlElement
    private String error;

    @XmlElement
    private Collection<String> params = new ArrayList<String>();
}

