/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.sfmc.connector.core.servlets;

import com.aem.sfmc.connector.core.beans.OAuthRequest;
import com.aem.sfmc.connector.core.beans.OAuthResponse;
import com.aem.sfmc.connector.core.beans.SFMCException;
import com.aem.sfmc.connector.core.services.SFMCConnectorService;
import com.aem.sfmc.connector.core.util.SFMCConnectorConstants;
import org.apache.http.MethodNotSupportedException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = {Servlet.class})
@SlingServletResourceTypes(resourceSuperType = "cq/cloudserviceconfigs/components/configpage", resourceTypes = "/apps/aem-sfmc-connector/components/page/configpage", methods = HttpConstants.METHOD_GET, extensions = "json", selectors = "connect")
@ServiceDescription("SFMC Connector Servlet")
@SuppressWarnings("squid:S1948")
public class SFMCConfigConnectorServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(SFMCConfigConnectorServlet.class);

    @Reference
    transient SFMCConnectorService sfmcConnectorService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {

        LOG.info("In Servlet..");

        String host = request.getRequestParameter(SFMCConnectorConstants.REQUEST_CONFIG_CONNECT_PARAM_AUTH_URL).getString();
        String grantType = request.getRequestParameter(SFMCConnectorConstants.REQUEST_CONFIG_CONNECT_PARAM_GRANT_TYPE).getString();
        String clientId = request.getRequestParameter(SFMCConnectorConstants.REQUEST_CONFIG_CONNECT_PARAM_CLIENT_ID).getString();
        String clientSecret = request.getRequestParameter(SFMCConnectorConstants.REQUEST_CONFIG_CONNECT_PARAM_CLIENT_SECRET).getString();
        String accountId = request.getRequestParameter(SFMCConnectorConstants.REQUEST_CONFIG_CONNECT_PARAM_ACCOUNT_ID).getString();

        OAuthResponse oAuthResponse;
        try {
            OAuthRequest oAuthRequest = new OAuthRequest(clientId, clientSecret, grantType, accountId);
            oAuthResponse = sfmcConnectorService.getAuthTokenResponse(oAuthRequest, host);

            LOG.info("Response status : {}", oAuthResponse.getStatusCode());
            LOG.info("Response received : {}", oAuthResponse.getResponse());

            response.setStatus(oAuthResponse.getStatusCode());
            response.getWriter().write(oAuthResponse.getResponse());
        } catch (SFMCException e) {
            LOG.error("SFMC Exception {} : {} : {}", e.getErrorCode(), e.getErrorMsg(), e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (MethodNotSupportedException e) {
            LOG.error("SFMC Unknown Exception {} ", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
