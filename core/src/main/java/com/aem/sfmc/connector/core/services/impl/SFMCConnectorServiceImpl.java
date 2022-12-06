package com.aem.sfmc.connector.core.services.impl;

import com.aem.sfmc.connector.core.beans.*;
import com.aem.sfmc.connector.core.services.RestClient;
import com.aem.sfmc.connector.core.services.SFMCConnectorConfigService;
import com.aem.sfmc.connector.core.services.SFMCConnectorService;
import com.aem.sfmc.connector.core.util.SFMCConnectorConstants;
import com.aem.sfmc.connector.core.util.SFMCConnectorUtil;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.MethodNotSupportedException;
import org.apache.jackrabbit.oak.commons.IOUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.engine.SlingRequestProcessor;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component(service = SFMCConnectorService.class, immediate = true)
public class SFMCConnectorServiceImpl implements SFMCConnectorService {

    private static final Logger LOG = LoggerFactory.getLogger(SFMCConnectorServiceImpl.class);

    @Reference
    RestClient restClient;

    @Reference
    SFMCConnectorConfigService sfmcConnectorConfigService;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    RequestResponseFactory requestResponseFactory;

    @Reference
    SlingRequestProcessor slingRequestProcessor;


    @Override
    public OAuthResponse getAuthTokenResponse(OAuthRequest oAuthRequest, String authUrl) throws SFMCException, MethodNotSupportedException, IOException {

        Gson gson = new Gson();
        String requestJson = gson.toJson(oAuthRequest);
        BaseRequest request = new BaseRequest(authUrl, HttpMethodType.POST, requestJson);

        APIResponse apiResponse = restClient.sendRequest(request, null);

        if (apiResponse.getStatusCode() > 300) {
            LOG.error("Error Fetching access_token from SFMC : {} : {}", apiResponse.getStatusCode(), apiResponse.getResponse());
            throw new SFMCException(String.valueOf(apiResponse.getStatusCode()), apiResponse.getResponse());
        }

        OAuthResponse oAuthResponse = gson.fromJson(apiResponse.getResponse(), OAuthResponse.class);
        oAuthResponse.setStatusCode(apiResponse.getStatusCode());
        oAuthResponse.setResponse(apiResponse.getResponse());

        return oAuthResponse;
    }

    @Override
    public OAuthResponse getAuthTokenResponse(Page campaignPage) throws SFMCException, MethodNotSupportedException, IOException {

        if (null == campaignPage) {
            throw new SFMCException("SFMC:PAGE_NOT_FOUND", "Requested Page is null");
        }

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = SFMCConnectorUtil.getResourceResolver(resourceResolverFactory, SFMCConnectorConstants.SUB_SERVICE_NAME);
            ValueMap props = getSFMCCloudConfig(campaignPage.getContentResource(), resourceResolver).getValueMap();
            return getAuthTokenResponse(new OAuthRequest(props.get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_CLIENT_ID, String.class), props.get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_CLIENT_SECRET, String.class), props.get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_GRANT_TYPE, String.class), props.get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_ACCOUNT_ID, String.class)), props.get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_AUTH_URL, String.class));
        } catch (LoginException ex) {
            LOG.error("Login Exception, fetching service resource resolver.", ex);
            throw new SFMCException("SFMC:RESOURCE_RESOLVER_LOGIN_ERROR", "Login Exception, fetching service resource resolver.");
        } finally {
            SFMCConnectorUtil.closeResourceResolver(resourceResolver);
        }

    }

    @Override
    public APIResponse createFolder(CreateFolderRequest createFolderRequest, String authToken, String requestUrl) throws MethodNotSupportedException, IOException {

        Gson gson = new Gson();
        String requestJson = gson.toJson(createFolderRequest);

        LOG.debug("Creating folder {} under {}", createFolderRequest.getName(), createFolderRequest.getParentId());
        BaseRequest request = new BaseRequest(requestUrl + sfmcConnectorConfigService.getCategoryAPIPath(), HttpMethodType.POST, requestJson);

        return restClient.sendRequest(request, getAuthHeaderMap(authToken));
    }

    @Override
    public APIResponse syncEmail(String authToken, String requestUrl, Page campaignPage, ResourceResolver resourceResolver) throws MethodNotSupportedException, SFMCException, IOException, ServletException {

        APIResponse emailResponse;
        ResourceResolver serviceResourceResolver = null;

        if (null == campaignPage) {
            throw new SFMCException("SFMC:PAGE_NOT_FOUND", "Requested Page is null");
        }
        LOG.debug("Campaign page path : {}", campaignPage.getPath());

        try {
            serviceResourceResolver = SFMCConnectorUtil.getResourceResolver(resourceResolverFactory, SFMCConnectorConstants.SUB_SERVICE_NAME);

            Resource sfmcCloudConfig = getSFMCCloudConfig(campaignPage.getContentResource(), serviceResourceResolver);
            String emailRoot = sfmcCloudConfig.getValueMap().get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_EMAIL_ROOT, String.class);

            int sfmcParentFolderId = getSFMCRootFolderId(authToken, requestUrl, sfmcCloudConfig, serviceResourceResolver);

            sfmcParentFolderId = syncFolders(authToken, requestUrl, campaignPage, emailRoot, resourceResolver, sfmcParentFolderId);
            String htmlContent = getHTMLContent(campaignPage, resourceResolver);
            boolean isCreateFlow = true;
            int sfmcEmailId = -1;

            Resource campaignPageContentResource = campaignPage.getContentResource();
            ValueMap props = campaignPageContentResource.getValueMap();
            if (props.containsKey(SFMCConnectorConstants.EMAIL_PROP_SFMC_EMAIL_ID)) {
                sfmcEmailId = props.get(SFMCConnectorConstants.EMAIL_PROP_SFMC_EMAIL_ID, -1);
                if (sfmcEmailId >= 0) {
                    isCreateFlow = false;
                }
            }
            if (StringUtils.isNotBlank(htmlContent)) {

                String htmlEmailRequest = getSyncEmailRequest(campaignPage, htmlContent, sfmcParentFolderId);

                if (isCreateFlow) {
                    BaseRequest request = new BaseRequest(requestUrl + sfmcConnectorConfigService.getAssetAPIPath(), HttpMethodType.POST, htmlEmailRequest);
                    emailResponse = restClient.sendRequest(request, getAuthHeaderMap(authToken));

                    try {
                        updateEmailProperties(emailResponse, campaignPageContentResource, resourceResolver);
                    } catch (JSONException ex) {
                        LOG.error("Error in email properties update..", ex);
                        throw new SFMCException("SFMC:WRITEBACK_ERROR", "SFMC information couldn't be saved back to the campaign : " + campaignPage.getPath());
                    }
                } else {
                    BaseRequest request = new BaseRequest(requestUrl + sfmcConnectorConfigService.getAssetAPIPath() + SFMCConnectorConstants.PATH_SEPERATOR + sfmcEmailId, HttpMethodType.PATCH, htmlEmailRequest);
                    emailResponse = restClient.sendRequest(request, getAuthHeaderMap(authToken));
                }
            } else {
                LOG.info("HTML Email content is empty OR Campaign page couldn't be resolved.");
                throw new SFMCException("SFMC:EMPTY_PAGE", "Error while building Json from page : " + campaignPage.getPath());
            }

        } catch (LoginException ex) {
            LOG.error("Login Exception, fetching service resource resolver.", ex);
            throw new SFMCException("SFMC:RESOURCE_RESOLVER_LOGIN_ERROR", "Login Exception, fetching service resource resolver.");
        } finally {
            SFMCConnectorUtil.closeResourceResolver(serviceResourceResolver);
        }

        return emailResponse;
    }

    private Resource getSFMCCloudConfig(Resource campaignPageResource, ResourceResolver resourceResolver) throws SFMCException {

        if (null == campaignPageResource) {
            throw new SFMCException("SFMC:RESOURCE_NULL", "Requested Page resource is null");
        }

        InheritanceValueMap inheritedProp = new HierarchyNodeInheritanceValueMap(campaignPageResource);
        String[] services = inheritedProp.getInherited(SFMCConnectorConstants.TYPE_CLOUD_SERVICE_CONFIGS, new String[]{});
        Resource cloudConfig = null;

        ConfigurationManager cfgMgr = resourceResolver.adaptTo(ConfigurationManager.class);
        if (null != cfgMgr) {
            Configuration cfg = cfgMgr.getConfiguration(SFMCConnectorConstants.SFMC_CONFIGURATION_NAME, services);
            if (null != cfg) {
                cloudConfig = cfg.getContentResource();
                LOG.debug("Identified cloud config : {}", cfg.getPath());
            }
        }

        if (null == cloudConfig) {
            LOG.debug("SFMC Cloud Config not found ");
            throw new SFMCException("SFMC:CLOUD_SERVICE_NOT_FOUND", "SFMC Cloud Config not found");
        }

        return cloudConfig;
    }

    private int getSFMCRootFolderId(String authToken, String requestUrl, Resource sfmcCloudConfig, ResourceResolver resourceResolver) throws IOException, MethodNotSupportedException, SFMCException {

        int sfmcRootFolderId = -1;
        ValueMap sfmcCloudConfigProps = sfmcCloudConfig.getValueMap();

        if (sfmcCloudConfigProps.containsKey(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_SFMC_ROOT_FOLDER_ID)) {
            sfmcRootFolderId = sfmcCloudConfigProps.get(SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_SFMC_ROOT_FOLDER_ID, -1);
            LOG.debug("SFMC root folder Id from configuration : {}", sfmcRootFolderId);
        }

        if (sfmcRootFolderId < 0) {

            BaseRequest request = new BaseRequest(requestUrl + sfmcConnectorConfigService.getCategoryAPIPath() + SFMCConnectorConstants.PATH_QUERY_PARAM_SEPERATOR + SFMCConnectorConstants.API_GET_ROOT_FOLDER_QUERY_STRING, HttpMethodType.GET);

            APIResponse apiResponse = restClient.sendRequest(request, getAuthHeaderMap(authToken));

            if (SlingHttpServletResponse.SC_OK == apiResponse.getStatusCode()) {
                String response = apiResponse.getResponse();

                Gson gson = new Gson();
                GetFoldersResponse getFoldersResponse = gson.fromJson(response, GetFoldersResponse.class);

                if (null != getFoldersResponse && null != getFoldersResponse.getItems() && getFoldersResponse.getItems().length > 0 && null != getFoldersResponse.getItems()[0]) {
                    sfmcRootFolderId = getFoldersResponse.getItems()[0].getId();
                    LOG.debug("SFMC Root folder id from service : {}", sfmcRootFolderId);

                    updateFolderId(sfmcCloudConfig, SFMCConnectorConstants.SFMC_CLOUD_CONFIG_PROP_SFMC_ROOT_FOLDER_ID, sfmcRootFolderId, resourceResolver);
                }
            }
        }

        if (sfmcRootFolderId < 0) {
            throw new SFMCException("SFMC:NO_SFMC_ROOT_FOLDER", "Couldn't fetch the root folder for SFMC.");
        }
        return sfmcRootFolderId;
    }

    @SuppressWarnings("findsecbugs:PATH_TRAVERSAL_IN")
    private int syncFolders(String authToken, String requestUrl, Page campaignPage, String emailRoot, ResourceResolver resourceResolver, int sfmcParentFolderId) throws MethodNotSupportedException, IOException, SFMCException {

        Path folders = Paths.get(campaignPage.getPath());
        int numberOfParentLevels = folders.getNameCount() - 1;
        LOG.debug("Number of parent levels : {}", numberOfParentLevels);

        StringBuffer aemPath = new StringBuffer();

        aemPath.append(SFMCConnectorConstants.PATH_SEPERATOR);
        for (int i = 0; i < numberOfParentLevels; i++) {
            aemPath.append(folders.getName(i).toString());
            String folderPath = aemPath.toString();
            LOG.debug("Folder path : {}", folderPath);

            if (folderPath.contains(emailRoot)) {
                int sfmcFolderId = 0;
                Resource folderResource = resourceResolver.getResource(folderPath + SFMCConnectorConstants.JCR_CONTENT_PATH);
                ValueMap props = folderResource.getValueMap();
                if (props.containsKey(SFMCConnectorConstants.EMAIL_PROP_SFMC_FOLDER_ID)) {
                    sfmcFolderId = props.get(SFMCConnectorConstants.EMAIL_PROP_SFMC_FOLDER_ID, 0);
                    LOG.debug("SFMC folder id : {}", sfmcFolderId);
                }

                Page page = resourceResolver.getResource(folderPath).adaptTo(Page.class);

                if (sfmcFolderId != 0) {
                    sfmcParentFolderId = sfmcFolderId;
                    LOG.debug("Current folder exists in SFMC with id : {}. Skipping folder creation..", sfmcFolderId);
                } else {
                    CreateFolderRequest createFolderRequest = new CreateFolderRequest(page.getTitle(), sfmcParentFolderId);
                    APIResponse response = createFolder(createFolderRequest, authToken, requestUrl);

                    if (SlingHttpServletResponse.SC_CREATED == response.getStatusCode()) {
                        try {
                            JSONObject obj = new JSONObject(response.getResponse());
                            sfmcParentFolderId = obj.getInt(SFMCConnectorConstants.SFMC_RESPONSE_KEY_ID);
                            updateFolderId(folderResource, SFMCConnectorConstants.EMAIL_PROP_SFMC_FOLDER_ID, sfmcParentFolderId, resourceResolver);
                        } catch (JSONException ex) {
                            LOG.error("Error in folder update..", ex);
                            break;
                        }
                    } else {
                        throw new SFMCException("SFMC:FOLDER_SYNC_FAILED", StringUtils.isNotBlank(response.getResponse()) ? response.getResponse() : "Error syncing AEM folders to SFMC.");
                    }

                }
            }

            aemPath.append(SFMCConnectorConstants.PATH_SEPERATOR);
        }
        return sfmcParentFolderId;
    }

    private String getHTMLContent(Page campaignPage, ResourceResolver resourceResolver) throws ServletException, IOException {

        String campaignPagePath = campaignPage.getPath();
        LOG.debug("Campaign page path for html req : {}", campaignPagePath);

        HttpServletRequest htmlPageRequest = requestResponseFactory.createRequest(HttpMethodType.GET.toString(), campaignPagePath + SFMCConnectorConstants.EXTENSION_HTML);
        WCMMode.DISABLED.toRequest(htmlPageRequest);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            HttpServletResponse htmlPageResponse = requestResponseFactory.createResponse(outputStream);
            slingRequestProcessor.processRequest(htmlPageRequest, htmlPageResponse, resourceResolver);
            return outputStream.toString();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private String getSyncEmailRequest(Page campaignPage, String htmlContent, int sfmcParentFolderId) {

        String name = campaignPage.getName();
        LOG.debug("Page title : {}", name);
        boolean channel_email = true;
        boolean channel_web = false;
        int categoryId = sfmcParentFolderId; // 606485
        int parentCategoryId = 0;
        String categoryName = "Content Builder";
        String assetType = "htmlemail";
        int assetTypeId = 208;

        Gson gson = new Gson();
        CreateEmailRequest emailRequest = new CreateEmailRequest();

        emailRequest.setName(name);

        Channels channels = new Channels();
        channels.setEmail(channel_email);
        channels.setWeb(channel_web);
        emailRequest.setChannels(channels);

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        category.setParentId(parentCategoryId);
        emailRequest.setCategory(category);

        Views views = new Views();
        EmailHTML emailHTML = new EmailHTML();
        emailHTML.setContent(htmlContent);
        views.setHtml(emailHTML);
        views.setPreheader(new EmailPreHeader());
        views.setSubjectline(new EmailSubject());
        views.setText(new EmailText());
        emailRequest.setViews(views);

        AssetType assetTypeObj = new AssetType();
        assetTypeObj.setName(assetType);
        assetTypeObj.setId(assetTypeId);
        emailRequest.setAssetType(assetTypeObj);

        return gson.toJson(emailRequest);
    }

    private void updateFolderId(Resource folderResource, String folderPropertyName, int folderId, ResourceResolver resourceResolver) throws PersistenceException {

        if (folderId > 0) {
            LOG.debug("Updating the folder resource with property : {} and id : {}", folderPropertyName, folderId);
            ModifiableValueMap updatedProps = folderResource.adaptTo(ModifiableValueMap.class);
            updatedProps.put(folderPropertyName, folderId);
            resourceResolver.commit();
            LOG.debug("Update successful.");
        }
    }

    private void updateEmailProperties(APIResponse response, Resource emailResource, ResourceResolver resourceResolver) throws JSONException, PersistenceException {

        if (SlingHttpServletResponse.SC_CREATED == response.getStatusCode()) {
            String jsonResponse = response.getResponse();
            JSONObject obj = new JSONObject(jsonResponse);
            int emailId = obj.getInt(SFMCConnectorConstants.SFMC_RESPONSE_KEY_ID);
            String emailName = obj.getString(SFMCConnectorConstants.SFMC_RESPONSE_KEY_NAME);
            LOG.debug("Email created with name : {}, Updating the campaign page resource with id : {}", emailName, emailId);

            ModifiableValueMap updatedProps = emailResource.adaptTo(ModifiableValueMap.class);
            updatedProps.put(SFMCConnectorConstants.EMAIL_PROP_SFMC_EMAIL_ID, emailId);
            updatedProps.put(SFMCConnectorConstants.EMAIL_PROP_SFMC_EMAIL_NAME, emailName);
            resourceResolver.commit();
            LOG.debug("Update successful.");
        }
    }

    public APIResponse triggerEmail(String authToken, String requestUrl, String payload)
            throws MethodNotSupportedException, IOException {

        BaseRequest request = new BaseRequest(requestUrl + sfmcConnectorConfigService.getTriggerMailAPIPath(),
                HttpMethodType.POST, payload);
        APIResponse emailResponse = restClient.sendRequest(request, getAuthHeaderMap(authToken));

        return emailResponse;

    }

    private Map<String, String> getAuthHeaderMap(String authToken) {

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + authToken);

        return headerMap;
    }
}
