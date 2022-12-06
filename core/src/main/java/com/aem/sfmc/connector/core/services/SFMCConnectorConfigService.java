package com.aem.sfmc.connector.core.services;

public interface SFMCConnectorConfigService {

    public String getAssetAPIPath();

    public String getCategoryAPIPath();

    public String getTriggerMailAPIPath();

    public int getConnectionTimeOut();

    public int getSocketTimeOut();

    public int getRequestTimeOut();
}
