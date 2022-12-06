package com.aem.sfmc.connector.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AEM SFMC Connector - SFMC Integration Service Configuration", description = "SFMC Asset Upload Service Configuration")
public @interface SFMCAssetUploadServiceConfig {

	@AttributeDefinition(name = "Upload Asset API Path", description = "SFMC upload asset api path", type = AttributeType.STRING)
	public String getAssetUploadApiPath() default "asset/v1/content/assets";

	@AttributeDefinition(name = "Client Id", description = "SFMC client ID", type = AttributeType.STRING)
	public String getClientId() default "llcvwjpbhd3lopo4qv0y7idy";

	@AttributeDefinition(name = "Client Secret", description = "SFMC client secret", type = AttributeType.STRING)
	public String getClientSecret() default "7vFIO7mrGbiNqruVorV3tAhc";

	@AttributeDefinition(name = "Grant Type", description = "Request Grant Type", type = AttributeType.STRING)
	public String getGrantType() default "client_credentials";

	@AttributeDefinition(name = "Account Id", description = "SFMC Account ID", type = AttributeType.STRING)
	public String getAccountId() default "Deloitte DTTL";
	
	@AttributeDefinition(name = "Category ID", description = "SFMC Category ID", type = AttributeType.INTEGER)
	public int getSFMCCategoryId() default 702278;
	
	@AttributeDefinition(name = "Category Name", description = "SFMC Category Name/ Folder name", type = AttributeType.STRING)
	public String getSFMCCategoryName() default "AEM Auto Synced Assets";

	@AttributeDefinition(name = "Authentication URL", description = "SFMC Authentication URL", type = AttributeType.STRING)
	public String getAuthUrl() default "https://mc4by0xw84s11pznjgq1c45n7qr0.auth.marketingcloudapis.com/v2/token";

	@AttributeDefinition(name = "Base REST URL", description = "SFMC Base REST URL", type = AttributeType.STRING)
	public String getBaseRESTUrl() default "https://mc4by0xw84s11pznjgq1c45n7qr0.rest.marketingcloudapis.com/";
	
}