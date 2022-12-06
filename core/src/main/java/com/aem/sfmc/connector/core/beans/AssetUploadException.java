package com.aem.sfmc.connector.core.beans;

/**
 * @author bbashire
 * 
 * Exception is thrown when there is an error while uploading assets AEM/External Application.
 */
public class AssetUploadException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final String errorMsg;

	public AssetUploadException(String errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public AssetUploadException(String errorCode, String errorMsg, Exception cause) {
		super(errorMsg, cause);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}