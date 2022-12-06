package com.aem.sfmc.connector.core.services;

import java.io.IOException;

import javax.servlet.ServletException;

import com.aem.sfmc.connector.core.beans.*;
import org.apache.http.MethodNotSupportedException;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.Page;

public interface SFMCConnectorService {

    public OAuthResponse getAuthTokenResponse(OAuthRequest oAuthRequest, String authUrl) throws SFMCException, MethodNotSupportedException, IOException;

    public OAuthResponse getAuthTokenResponse(Page campaignPageResource) throws SFMCException, MethodNotSupportedException, IOException;

    public APIResponse createFolder(CreateFolderRequest createFolderRequest, String authToken, String requestUrl) throws MethodNotSupportedException, IOException;

    public APIResponse syncEmail(String authToken, String requestUrl, Page campaignPage, ResourceResolver resourceResolver) throws MethodNotSupportedException, SFMCException, IOException, ServletException;

	public APIResponse triggerEmail(String access_token, String rest_instance_url, String payload) throws MethodNotSupportedException, IOException;
}
