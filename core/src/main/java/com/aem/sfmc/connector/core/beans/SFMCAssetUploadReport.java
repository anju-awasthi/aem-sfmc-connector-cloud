package com.aem.sfmc.connector.core.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class SFMCAssetUploadReport {

	private List<AssetUploadStatus> uploadReport;

	public List<AssetUploadStatus> getUploadReport() {
		if (this.uploadReport == null) {
			this.uploadReport = new ArrayList<AssetUploadStatus>();
		}
		return this.uploadReport;
	}

	public void setUploadReport(final ArrayList<AssetUploadStatus> uploadReport) {
		this.uploadReport = uploadReport;
	}
	
	@Override
	public String toString() {
		StringBuilder uploadReportString = new StringBuilder();
		if(CollectionUtils.isNotEmpty(this.uploadReport)) {
			for(AssetUploadStatus status : this.uploadReport) {
				uploadReportString.append(status.toString()).append("\n\n");
			}
		} 
		return uploadReportString.toString();
	}
}