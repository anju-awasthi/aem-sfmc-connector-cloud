package com.aem.sfmc.connector.core.util;

/**
 * This class holds constant values which are referenced and used across the AEM
 * backend landscape.
 */
public final class SFMCConnectorConstants {

	/**
	 * Class Constructor.
	 */
	private SFMCConnectorConstants() {
	}

	/** The Constant HTTPS. */
	public static final String HTTPS = "https";

	/** The Constant HTTP. */
	public static final String HTTP = "http";

	public static final String PATH_SEPERATOR = "/";

	public static final String PATH_QUERY_PARAM_SEPERATOR = "?";

	public static final String REQUEST_CONFIG_CONNECT_PARAM_AUTH_URL = "authurl";

	public static final String REQUEST_CONFIG_CONNECT_PARAM_GRANT_TYPE = "grant_type";

	public static final String REQUEST_CONFIG_CONNECT_PARAM_CLIENT_ID = "client_id";

	public static final String REQUEST_CONFIG_CONNECT_PARAM_CLIENT_SECRET = "client_secret";

	public static final String REQUEST_CONFIG_CONNECT_PARAM_ACCOUNT_ID = "account_id";

	public static final String SFMC_CLOUD_CONFIG_PROP_AUTH_URL = "authUrl";

	public static final String SFMC_CLOUD_CONFIG_PROP_GRANT_TYPE = "grantType";

	public static final String SFMC_CLOUD_CONFIG_PROP_CLIENT_ID = "clientId";

	public static final String SFMC_CLOUD_CONFIG_PROP_CLIENT_SECRET = "clientSecret";

	public static final String SFMC_CLOUD_CONFIG_PROP_ACCOUNT_ID = "accountId";

	public static final String SFMC_CLOUD_CONFIG_PROP_EMAIL_ROOT = "emailRoot";

	public static final String SFMC_CLOUD_CONFIG_PROP_SFMC_ROOT_FOLDER_ID = "sfmcRootFolderId";

	public static final String EMAIL_PROP_SFMC_EMAIL_ID = "sfmcEmailId";

	public static final String EMAIL_PROP_SFMC_EMAIL_NAME = "sfmcEmailName";

	public static final String EMAIL_PROP_SFMC_FOLDER_ID = "sfmcFolderId";

	public static final String TYPE_CLOUD_SERVICE_CONFIGS = "cq:cloudserviceconfigs";

	public static final String SUB_SERVICE_NAME = "sfmcService";

	public static final String SFMC_CONFIGURATION_NAME = "sfmc";

	public static final String JCR_CONTENT_PATH = "/jcr:content";

	public static final String EXTENSION_HTML = ".html";

	public static final String SFMC_RESPONSE_KEY_ID = "id";

	public static final String SFMC_RESPONSE_KEY_NAME = "name";

	public static final String API_GET_ROOT_FOLDER_QUERY_STRING = "%24filter=parentId%20eq%200";

}
