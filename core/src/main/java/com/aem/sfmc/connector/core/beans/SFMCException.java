package com.aem.sfmc.connector.core.beans;

public class SFMCException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorCode;
	
	private String errorMsg;
	
	public SFMCException(String errorCode,String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public SFMCException(String errorCode, String errorMsg, Exception cause) {
		super(cause);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
