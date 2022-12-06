package com.aem.sfmc.connector.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AEM-SFMC Connector Configuration", description = "Configurations for AEM-SFMC Connector")
public @interface SFMCConnectorConfig {

    @AttributeDefinition(name = "Asset REST API Path", description = "REST API Path for managing marketing content(assets).", type = AttributeType.STRING)
    public String assetAPIPath() default "asset/v1/content/assets";

    @AttributeDefinition(name = "Category Rest API Path", description = "REST API Path for managing categories.", type = AttributeType.STRING)
    public String categoryAPIPath() default "asset/v1/content/categories";

    @AttributeDefinition(name = "Connection Timeout", description = "Connection timeout value in ms.", type = AttributeType.INTEGER)
    public int connectionTimeOut() default 0;

    @AttributeDefinition(name = "Socket Timeout", description = "Socket timeout value in ms.", type = AttributeType.INTEGER)
    public int socketTimeOut() default 0;

    @AttributeDefinition(name = "Request Timeout", description = "Request timeout value in ms.", type = AttributeType.INTEGER)
    public int requestTimeOut() default 0;
    
    @AttributeDefinition(name = "Trigger Mail API Path", description = "REST API Path for triggering email", type = AttributeType.STRING)
    public String triggerMailAPIPath() default "messaging/v1/messageDefinitionSends/key:263479/send";

}
