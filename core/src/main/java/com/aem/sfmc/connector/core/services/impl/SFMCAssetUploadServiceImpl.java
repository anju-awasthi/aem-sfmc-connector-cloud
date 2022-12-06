package com.aem.sfmc.connector.core.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.MethodNotSupportedException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.sfmc.connector.core.beans.APIResponse;
import com.aem.sfmc.connector.core.beans.AssetType;
import com.aem.sfmc.connector.core.beans.AssetUploadException;
import com.aem.sfmc.connector.core.beans.AssetUploadStatus;
import com.aem.sfmc.connector.core.beans.BaseRequest;
import com.aem.sfmc.connector.core.beans.HttpMethodType;
import com.aem.sfmc.connector.core.beans.OAuthRequest;
import com.aem.sfmc.connector.core.beans.OAuthResponse;
import com.aem.sfmc.connector.core.beans.SFMCAssetType;
import com.aem.sfmc.connector.core.beans.SFMCAssetUploadReport;
import com.aem.sfmc.connector.core.beans.SFMCAssetUploadRequest;
import com.aem.sfmc.connector.core.beans.SFMCCategory;
import com.aem.sfmc.connector.core.beans.SFMCErrorResponse;
import com.aem.sfmc.connector.core.beans.SFMCException;
import com.aem.sfmc.connector.core.configs.SFMCAssetUploadServiceConfig;
import com.aem.sfmc.connector.core.constants.SFMCConstants;
import com.aem.sfmc.connector.core.services.DamUtilityService;
import com.aem.sfmc.connector.core.services.RestClient;
import com.aem.sfmc.connector.core.services.SFMCAssetUploadService;
import com.aem.sfmc.connector.core.services.SFMCConnectorService;
import com.aem.sfmc.connector.core.services.utility.JacksonMapperService;
import com.day.cq.dam.api.Asset;
import com.google.gson.Gson;

@Component(service = SFMCAssetUploadService.class, immediate = true)
@Designate(ocd = SFMCAssetUploadServiceConfig.class)
public class SFMCAssetUploadServiceImpl implements SFMCAssetUploadService{
	
	private static final Logger logger = LoggerFactory.getLogger(SFMCAssetUploadServiceImpl.class);
	
	private SFMCAssetUploadServiceConfig config;
	private Map<String, Integer> assetTypeMap;
	private Gson gson;
	@Reference
	private DamUtilityService damUtilityService;
	
	@Reference
	private SFMCConnectorService sfmcConnectorService;
	
	@Reference
	private RestClient restClient;
	
	@Reference
	private JacksonMapperService jacksonMapper;
	
	@Activate
	@Modified
	public void activate(SFMCAssetUploadServiceConfig config) {
		this.config = config;
		this.gson = new Gson();
		initializeAssetTypeMap();
		
	}

	private void initializeAssetTypeMap() {
		assetTypeMap = new HashMap<String, Integer>();
		final SFMCAssetType sfmcAssetType = damUtilityService.getSFMCAssetTypesFromJson();
		if (sfmcAssetType != null && CollectionUtils.isNotEmpty(sfmcAssetType.getAssetTypes())) {
			for (AssetType assetType : sfmcAssetType.getAssetTypes()) {
				this.assetTypeMap.put(assetType.getName(), assetType.getId());
			}
		}

	}

	@Override
	public SFMCAssetUploadReport uploadAssets() {
		final SFMCAssetUploadReport uploadReport = new SFMCAssetUploadReport();
		logger.info("Trying to sync sfmc assets.......");
		try {
			// get asset list to be synced with SFMC 
			final List<Asset> assetsList = damUtilityService.getAssetsListForSFMC();
			logger.info("Asset List size : {}", assetsList.size());
			if (CollectionUtils.isNotEmpty(assetsList)) {
				for (Asset asset : assetsList) {
                      logger.info("trying to sync asset to SFMC : {}", asset.getName());
					// authenticate sfmc
					final OAuthResponse authResponse = sfmcConnectorService.getAuthTokenResponse(getAuthRequest(), config.getAuthUrl());
					if (authResponse != null) {
						
						String requestUrl = authResponse.getRest_instance_url() + this.config.getAssetUploadApiPath();
						HttpMethodType httpMethodType = HttpMethodType.POST;
						
						final String sfmcAssetId = damUtilityService.getPropertyValueFromAsset(asset, SFMCConstants.SFMC_ASSET_ID);
						if (StringUtils.isNotBlank(sfmcAssetId)) {
							// build sfmc post request url
							requestUrl =  requestUrl + SFMCConstants.FORWARD_SLASH + Integer.parseInt(sfmcAssetId);
							httpMethodType = HttpMethodType.PATCH;
							logger.info("Post call request URL : {}", requestUrl);
						}
						BaseRequest uploadAssetRequest;
						try {

							uploadAssetRequest = new BaseRequest(requestUrl, httpMethodType, getAssetUploadRequestData(asset));

						} catch (final AssetUploadException exception) {

							logger.error("Error occured while creating asset upload request for {} : {}",
									asset.getPath(), exception.getErrorMsg()); 
							updateAssetUploadReport(uploadReport, asset, false, exception.getErrorMsg(), null);
							continue;
						}

						// send asset to sfmc
						final APIResponse uploadAssetResponse = restClient.sendRequest(uploadAssetRequest, getAuthHeaderMap(authResponse.getAccess_token()));
						// process sfmc response
						processAssetUploadResponse(asset, uploadAssetResponse, uploadReport);
					} else {
						logger.error("Could not authenticate with SFMC");
					}
				}
			} else {
				logger.info("There are no assets to be sent to SFMC");
			}
			

		} catch (MethodNotSupportedException | SFMCException | IOException  exception) {
			logger.error("Exception occured while performing SFMC assest upload operation : {}", exception.getLocalizedMessage());
		}
		return uploadReport;

	}

	@SuppressWarnings("squid:S1854")
	private void processAssetUploadResponse(final Asset asset, final APIResponse uploadAssetResponse,
			final SFMCAssetUploadReport uploadReport) {

		if (uploadAssetResponse.getStatusCode() == 201) {
			String response = uploadAssetResponse.getResponse();
			Map <String,Object> responseMap = jacksonMapper.convertJsonToMap(response);
			String sfmcAssetId = responseMap.get("id").toString();
			updateAssetUploadReport(uploadReport, asset, true, SFMCConstants.SFMC_ASSET_UPLOADED_SUCCESSFULLY, sfmcAssetId);

			logger.debug("Asset {} uploaded successfully to SFMC", asset.getName());
		} else if (uploadAssetResponse.getStatusCode() == 200) {
			final String response = uploadAssetResponse.getResponse();
			final Map<String, Object> responseMap = jacksonMapper.convertJsonToMap(response);
			final String sfmcAssetId = responseMap.get("id").toString();
			updateAssetUploadReport(uploadReport, asset, true, SFMCConstants.SFMC_ASSET_UPDATED_SUCCESSFULLY, sfmcAssetId);
			logger.debug("Asset {} updated successfully to SFMC", asset.getName());
		} else {
			if (StringUtils.isNotBlank(uploadAssetResponse.getResponse())) {
				final SFMCErrorResponse errorResponse = this.jacksonMapper.convertJsonToObject(uploadAssetResponse.getResponse(), SFMCErrorResponse.class);
				String errorMessage = StringUtils.EMPTY;
				if (errorResponse != null) {
					if (CollectionUtils.isNotEmpty(errorResponse.getValidationErrors()) && errorResponse.getValidationErrors().get(0) != null) {
						errorMessage = errorResponse.getValidationErrors().get(0).getMessage();
					} else {
						errorMessage = errorResponse.getMessage();
					}
					logger.debug("Error uploading Asset {} to SFMC ::{}", asset.getName(), errorMessage);
					updateAssetUploadReport(uploadReport, asset, false, errorMessage, null);
				}
				
			}

		}
	}

	private String getAssetUploadRequestData(final Asset asset) throws AssetUploadException {
		final SFMCAssetUploadRequest assetUploadRequest = new SFMCAssetUploadRequest();
		if (asset != null) {
			assetUploadRequest.setName(asset.getName());
			assetUploadRequest.setAssetType(getSFMCAssetType(asset));
			assetUploadRequest.setCategory(getSFMCCategory());
			InputStream inputStream = asset.getOriginal().getStream();
			String assetString = null;
			try {
				byte[] assestBytes = IOUtils.toByteArray(inputStream);
				assetString = Base64.getEncoder().encodeToString(assestBytes);
			} catch (IOException e) {
				logger.error("Error occured while reading asset bytes : {}", e.getLocalizedMessage());
				throw new AssetUploadException("SFMC1001", SFMCConstants.SFMC_EMPTY_ASSET_BYTES_STREAM);
			}
			assetUploadRequest.setFile(assetString);
		}
		return this.gson.toJson(assetUploadRequest);
	}

	private SFMCCategory getSFMCCategory() {
		final SFMCCategory category = new SFMCCategory();
		category.setId(config.getSFMCCategoryId());
		category.setName(config.getSFMCCategoryName());
		return category;
	}

	private AssetType getSFMCAssetType(final Asset asset) throws AssetUploadException  {
		final String assestPath = asset.getPath();
		String assetExtention = null;
		if (StringUtils.isNotBlank(assestPath)) {
			final String[] paths = assestPath.split("\\.");
			if (paths.length > 1) {
				assetExtention = paths[1];
				logger.debug("Asset Path is : {}", assestPath);
				logger.debug("Asset Extesion is : {}", assetExtention);
			}
		}

		if (StringUtils.isNotBlank(assetExtention) && this.assetTypeMap.containsKey(assetExtention)) {
			return new AssetType(assetExtention, this.assetTypeMap.get(assetExtention).intValue());
		} else {
			throw new AssetUploadException("SFMC1000",SFMCConstants.SFMC_ASSET_UPLOAD_REQUEST_ERROR);
		}
	}

	private OAuthRequest getAuthRequest() throws SFMCException {
		return new OAuthRequest(config.getClientId(), config.getClientSecret(), config.getGrantType(), config.getAccountId());
	}
	
	private Map<String, String> getAuthHeaderMap(final String authToken) {
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization", "Bearer " + authToken);
		return headerMap;
	}
	
	private void updateAssetUploadReport(final SFMCAssetUploadReport uploadReport, final Asset assest, final boolean uploadStatus, final String message, final String sfmcAssetId) {
		final AssetUploadStatus assetUploadStatus  =  new AssetUploadStatus(assest.getName(), assest.getPath(), uploadStatus, message, sfmcAssetId);
		uploadReport.getUploadReport().add(assetUploadStatus);
	}

}