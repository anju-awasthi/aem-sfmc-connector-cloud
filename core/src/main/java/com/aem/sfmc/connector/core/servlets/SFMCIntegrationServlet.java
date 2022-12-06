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

import com.aem.sfmc.connector.core.beans.APIResponse;
import com.aem.sfmc.connector.core.beans.OAuthResponse;
import com.aem.sfmc.connector.core.beans.SFMCException;
import com.aem.sfmc.connector.core.services.SFMCConnectorService;
import com.aem.sfmc.connector.core.util.SFMCConnectorUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "services/sfmc/emailSync", methods = HttpConstants.METHOD_POST, extensions = "json", selectors = "email")
@ServiceDescription("SFMC Integration Servlet")
@SuppressWarnings("squid:S1948")
public class SFMCIntegrationServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SFMCIntegrationServlet.class);

	@Reference
	transient SFMCConnectorService sfmcConnectorService;

	@Reference
	transient Replicator replicator;

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {

		LOG.info("In doPost..");
		handleCreateEmailRequest(request, response);

	}

	private void handleCreateEmailRequest(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) throws IOException {

		LOG.info("In handleCreateEmailRequest..");

		Session jcrSession = null;
		try {
			String pagePath = request.getParameter("pagePath");
			PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
			Page page = pageManager.getContainingPage(pagePath);
			OAuthResponse oAuthResponse = sfmcConnectorService.getAuthTokenResponse(page);

			if (SlingHttpServletResponse.SC_OK == oAuthResponse.getStatusCode()) {
				APIResponse apiResponse = sfmcConnectorService.syncEmail(oAuthResponse.getAccess_token(),
						oAuthResponse.getRest_instance_url(), page, request.getResourceResolver());
				LOG.info("Response received with status : {} and message : {}", apiResponse.getStatusCode(),
						apiResponse.getResponse());
				setServletResponse(response, apiResponse);

				LOG.info("Replicating page..");
				jcrSession = request.getResourceResolver().adaptTo(Session.class);
				replicator.replicate(jcrSession, ReplicationActionType.ACTIVATE, page.getPath());
			} else {
				setServletResponse(response, oAuthResponse);
			}

		} catch (SFMCException e) {
			LOG.error("SFMC Exception {} : {} : {}", e.getErrorCode(), e.getErrorMsg(), e);
			response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (MethodNotSupportedException | ServletException e) {
			LOG.error("SFMC Unknown Exception {} ", e);
			response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (ReplicationException e) {
			LOG.error("Error Occurred while replicating page  {} ", e);
		} finally {
			SFMCConnectorUtil.logoutSession(jcrSession);
		}
	}

	private void setServletResponse(SlingHttpServletResponse response, APIResponse apiResponse) throws IOException {

		response.setStatus(apiResponse.getStatusCode());
		response.getWriter().write(apiResponse.getResponse());
	}
}
