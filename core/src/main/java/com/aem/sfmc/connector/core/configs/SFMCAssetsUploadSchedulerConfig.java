package com.aem.sfmc.connector.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AEM SFMC Connector - SFMC asset upload Scheduler Config")
public @interface SFMCAssetsUploadSchedulerConfig {

    @AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
    String schedulerName() default "SFMC Assets upload";

    @AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
    boolean serviceEnabled() default true;

    @AttributeDefinition(name = "Expression", description = "Scheduler cron-job expression. Default: run every 60 min.", type = AttributeType.STRING)
    String schedulerExpression() default "0 */5 * ? * *";

}