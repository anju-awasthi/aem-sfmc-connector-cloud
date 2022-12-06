package com.aem.sfmc.connector.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("squid:S1948")
public class SFMCErrorResponse extends APIResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	public String message;
	public int errorcode;
	public String documentation;
	public List<ValidationError> validationErrors;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public List<ValidationError> getValidationErrors() {
		if (this.validationErrors == null) {
			this.validationErrors = new ArrayList<ValidationError>();
		}
		return validationErrors;
	}

	public void setValidationErrors(List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

}