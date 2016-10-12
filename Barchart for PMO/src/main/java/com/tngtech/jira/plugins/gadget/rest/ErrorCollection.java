package com.tngtech.jira.plugins.gadget.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class ErrorCollection
{
    public Collection<String> getErrorMessages() {
        return errorMessages;
    }

    public void addErrorMessage(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }

    public void addError(ValidationError error) {
        this.errors.add(error);
    }

    /**
     * Generic error messages
     */
    @XmlElement
    private Collection<String> errorMessages = new ArrayList<String>();

    /**
     * Errors specific to a certain field.
     */
    @XmlElement
    private Collection<ValidationError> errors = new ArrayList<ValidationError>();

    public boolean hasErrors() {
        return !(errors.isEmpty() && errorMessages.isEmpty());
    }
}

