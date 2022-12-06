package com.aem.sfmc.connector.core.beans;

/**
 * @author AH51926
 * 
 * POJO holds the asset upload related information like asset name, path, status etc.
 */
public class AssetUploadStatus {

	private String assetName;
	private String assetPath;
	private boolean uploadStatus;
	private String message;
	private String sfmcAssetId;

	public AssetUploadStatus(final String assetName, final String assetPath, final boolean uploadStatus, final String message, final String sfmcAssetId) {
		super();
		this.assetName = assetName;
		this.assetPath = assetPath;
		this.uploadStatus = uploadStatus;
		this.message = message;
		this.sfmcAssetId = sfmcAssetId;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public void setAssetPath(String assetPath) {
		this.assetPath = assetPath;
	}

	public boolean isUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(boolean uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getSfmcAssetId() {
		return sfmcAssetId;
	}

	public void setSfmcAssetId(String sfmcAssetId) {
		this.sfmcAssetId = sfmcAssetId;
	}

	@Override
	public String toString() {
		return "Assest Name :" + assetName + "\nAssest Path :" + assetPath + "\nStatus :" + uploadStatus + "\nMessage:"
				+ message;
	}

}