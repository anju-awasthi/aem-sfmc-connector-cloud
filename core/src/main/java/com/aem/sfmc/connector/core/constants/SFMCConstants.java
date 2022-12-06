/**
 * 
 */
package com.aem.sfmc.connector.core.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolverFactory;

/**
 * @author bbashire
 *
 */
public class SFMCConstants {

	private SFMCConstants() {
		 super();
	}
	
	public static final String SFMC_CONNECTOR_SUBSERVICE = "sfmcService";
	public static final Map<String, Object> SFMC_AUTO_INFO;
	
	/*common constants*/
	public static final String EQUAL_TO = "=";
	public static final String FORWARD_SLASH = "/";
	public static final String EMPTY_JSON_OBJECT = "{}";
	
	
	/*SFMC connector specific constants*/
	public static final String GENERIC_LIST_ROOT = "/etc/acs-commons/lists/aem-sfmc-connector/";
	public static final String SFMC_ASSET_ID = "sfmcAssetId";
	
	/*Messages constants*/
	public static final String SFMC_ASSET_UPLOADED_SUCCESSFULLY = "Asset uploaded successfully";
	public static final String SFMC_ASSET_UPLOAD_REQUEST_ERROR = "Unable to detect asset extention. Hence can not create asset upload request.";
	public static final String SFMC_EMPTY_ASSET_BYTES_STREAM = "Unable to read asset bytes stream";
	public static final String SFMC_ASSET_UPDATED_SUCCESSFULLY = "Asset updated successfully";
	
	static {
		Map<String,Object> infoMap = new HashMap<>();
		infoMap.put(ResourceResolverFactory.SUBSERVICE, SFMC_CONNECTOR_SUBSERVICE);
		SFMC_AUTO_INFO = Collections.unmodifiableMap(infoMap);
	}
}
