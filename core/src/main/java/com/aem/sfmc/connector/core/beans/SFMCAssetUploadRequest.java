package com.aem.sfmc.connector.core.beans;

public class SFMCAssetUploadRequest {

	private String name;
	private AssetType assetType;
	private SFMCCategory category;
	private String file;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public SFMCCategory getCategory() {
		return category;
	}

	public void setCategory(SFMCCategory category) {
		this.category = category;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}