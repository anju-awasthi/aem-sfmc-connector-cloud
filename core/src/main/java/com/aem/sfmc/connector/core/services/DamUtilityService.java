package com.aem.sfmc.connector.core.services;

import java.util.List;

import com.aem.sfmc.connector.core.beans.SFMCAssetType;
import com.aem.sfmc.connector.core.beans.SFMCAssetUploadReport;
import com.day.cq.dam.api.Asset;

public interface DamUtilityService {

	public List<Asset> getAssetsListForSFMC();

	public void updateAssetMetadata(SFMCAssetUploadReport uploadReport);

	public SFMCAssetType getSFMCAssetTypesFromJson();
	
	public String getPropertyValueFromAsset(Asset asset, String property);

}
