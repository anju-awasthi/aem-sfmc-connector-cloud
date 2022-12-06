package com.aem.sfmc.connector.core.services.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.sfmc.connector.core.beans.AssetUploadStatus;
import com.aem.sfmc.connector.core.beans.SFMCAssetType;
import com.aem.sfmc.connector.core.beans.SFMCAssetUploadReport;
import com.aem.sfmc.connector.core.constants.SFMCConstants;
import com.aem.sfmc.connector.core.services.DamUtilityService;
import com.aem.sfmc.connector.core.services.GenericListService;
import com.aem.sfmc.connector.core.services.utility.JacksonMapperService;
import com.aem.sfmc.connector.core.util.SearchUtility;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;

@Component(service = DamUtilityService.class, immediate = true)
public class DamUtilityServiceImpl implements DamUtilityService {

	private static final Logger logger = LoggerFactory.getLogger(DamUtilityServiceImpl.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private JacksonMapperService jacksonMapperService;

	@Reference
	private GenericListService genericListService;
	
	@Reference
	private SearchUtility searchUtility;

	public static final String SFMC_LIST_NAME = "sfmcAssetsData";
	public static final String SFMC_ASSET_PROP_NAME = "propertyNameForSFMCAsset";
	public static final String METADATA_PATH = "jcr:content/metadata";
	private static final String SFMC_QUERY_LIST_NAME = "sfmcQueryList";

	@Override
	@SuppressWarnings("squid:S2583")
	public List<Asset> getAssetsListForSFMC() {
		List<Asset> assetsList = new ArrayList<Asset>();
		logger.info("DamUtilityService -- start");

		try {
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(SFMCConstants.SFMC_AUTO_INFO);
			if (resourceResolver != null) {

				logger.info("Resource resolver is not NULL");

				final List<String> assetList = getAssetList();
				logger.info("Total Assets found by SFMC query : {}", assetList.size());

				for (String assetPath : assetList) {
					final Resource damResource = resourceResolver.getResource(assetPath);
					if (DamUtil.isAsset(damResource)) {

						final Asset asset = damResource.adaptTo(Asset.class);
						logger.info("Dam Asset Name ::{} ", asset.getName());
						final long lastModified = asset.getLastModified();
						final Date lastMofifiedDate = new Date(lastModified);
						final long timeDiff = new Date().getTime() - lastMofifiedDate.getTime();
						final long diffInHours = TimeUnit.MILLISECONDS.toHours(timeDiff);
						logger.debug("This asset is created/updated before : {} hours.", diffInHours);
						assetsList.add(asset);

					}

				}

			} else {
				logger.debug("Resource resolver is NULL");
			}
		} catch (final LoginException exception) {
			logger.error("Error occurred while reading asset to be synced with SFMC : {}", exception);
		}

		return assetsList;
	}

	@Override
	public void updateAssetMetadata(final SFMCAssetUploadReport uploadReport) {
		Map<String, String> sfmcDataMap =  genericListService.getGenericListAsMap(SFMCConstants.GENERIC_LIST_ROOT, SFMC_LIST_NAME);
		// TODO : Refactor to update metadata for one asset at atime
		try {
			
			ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(SFMCConstants.SFMC_AUTO_INFO);
			if (CollectionUtils.isNotEmpty(uploadReport.getUploadReport())) {
				final Session session = resourceResolver.adaptTo(Session.class);
				for (AssetUploadStatus assetUploadStatus : uploadReport.getUploadReport()) {

					final Resource resource = resourceResolver
							.getResource(assetUploadStatus.getAssetPath() + "/jcr:content/metadata");
					if (resource != null) {
						final Node assetNode = resource.adaptTo(Node.class);
						if (assetUploadStatus.isUploadStatus()) {
							// TODO : Update appropriate property on asset node
							assetNode.setProperty(sfmcDataMap.get(SFMC_ASSET_PROP_NAME), true);
							assetNode.setProperty("sfmcAssetId", assetUploadStatus.getSfmcAssetId());
						} else {
							assetNode.setProperty(sfmcDataMap.get(SFMC_ASSET_PROP_NAME), false);
						}
						// TODO : check if you can save several nodes at a time.
						session.save();
					}
				}

			} else {
				logger.debug("No asset uploaded to SFMC");
			}
		} catch (final RepositoryException | LoginException loginException) {
			logger.error("Error occured while updating asset metadata : {}", loginException.getLocalizedMessage());
		} 

	}

	@Override
	@SuppressWarnings("squid:S2221")
	public SFMCAssetType getSFMCAssetTypesFromJson() {
		Map<String, String> sfmcDataMap =  genericListService.getGenericListAsMap(SFMCConstants.GENERIC_LIST_ROOT, SFMC_LIST_NAME);
		SFMCAssetType sfmcAssetType = null;
		
		try {
			ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(SFMCConstants.SFMC_AUTO_INFO);
			if (resourceResolver != null) {
				final Resource resource = resourceResolver.getResource(sfmcDataMap.get("sfmcAssetTypeJsonPath"));

				if (resource != null) {
					final Asset asset = resource.adaptTo(Asset.class);
					if (asset != null) {
						final InputStream content = asset.getOriginal().getStream();
						sfmcAssetType = jacksonMapperService.convertJsonToObject(content, SFMCAssetType.class);
						if (sfmcAssetType != null) {
							logger.info("SFMC Asset Types List initialized with Size : {}", sfmcAssetType.getAssetTypes().size());
						}
					}
				}
			}

		} catch (LoginException exception) {
			logger.error("error :: {}", exception);
		} 
		return sfmcAssetType;
	}

	@Override
	public String getPropertyValueFromAsset(Asset asset, String property) {
		String propertyValue = null;
		try {
			ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(SFMCConstants.SFMC_AUTO_INFO);

			if (asset != null && StringUtils.isNotBlank(property)) {
				String path = asset.getPath() + "/" + METADATA_PATH;
				Node assetNode = resourceResolver.getResource(path).adaptTo(Node.class);
				if (assetNode != null && assetNode.hasProperty(property)) {
					propertyValue = assetNode.getProperty(property).getValue().toString();
				}

			}
		} catch (RepositoryException | LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propertyValue;
	}

	private List<String> getAssetList() { 
		final Map<String, String> sfmcQueryMap = genericListService.getGenericListAsMap(SFMCConstants.GENERIC_LIST_ROOT, SFMC_QUERY_LIST_NAME);
		logger.info("Query to Identify assets to be synced to SFMC : ", sfmcQueryMap.toString());
		return searchUtility.getResultPathList(sfmcQueryMap);
		
	}

}