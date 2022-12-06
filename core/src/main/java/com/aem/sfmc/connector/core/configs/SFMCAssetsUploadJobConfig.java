package com.aem.sfmc.connector.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AEM SFMC Connector - SFMC asset upload job consumer Config")
public @interface SFMCAssetsUploadJobConfig {

    @AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
    String schedulerName() default "SFMC Assets Upload JOB";

    @AttributeDefinition(name = "Enabled", description = "Enable Email Service ?", type = AttributeType.BOOLEAN)
    boolean emailServiceEnabled() default false;
    
    @AttributeDefinition(name = "Generic List Root path", description = "Generic List Root path", type = AttributeType.STRING)
    String genericListRoot() default "/etc/acs-commons/lists/aem-sfmc-connector/";

}