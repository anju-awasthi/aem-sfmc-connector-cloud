package com.aem.sfmc.connector.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.aem.sfmc.connector.core.configs.SFMCConnectorConfig;
import com.aem.sfmc.connector.core.services.SFMCConnectorConfigService;

@Component(immediate = true, service = SFMCConnectorConfigService.class)
@Designate(ocd = SFMCConnectorConfig.class)
public class SFMCConnectorConfigServiceImpl implements SFMCConnectorConfigService {

    private String assetAPIPath;

    private String categoryAPIPath;
    
    private String triggerMailAPIPath;

	private int connectionTimeOut;

    private int socketTimeOut;

    private int requestTimeOut;

    @Activate
    @Modified
    protected final void activate(SFMCConnectorConfig config){

        this.assetAPIPath = config.assetAPIPath();
        this.categoryAPIPath = config.categoryAPIPath();
        this.connectionTimeOut = config.connectionTimeOut();
        this.socketTimeOut = config.socketTimeOut();
        this.requestTimeOut = config.requestTimeOut();
        this.triggerMailAPIPath = config.triggerMailAPIPath();
    }

    @Override
    public String getAssetAPIPath() {
        return assetAPIPath;
    }

    @Override
    public String getCategoryAPIPath() {
        return categoryAPIPath;
    }

    @Override
    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    @Override
    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    @Override
    public int getRequestTimeOut() {
        return requestTimeOut;
    }
    
    @Override
	public String getTriggerMailAPIPath() {
		return triggerMailAPIPath;
	}
}
