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

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.http.MethodNotSupportedException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.sfmc.connector.core.beans.APIResponse;
import com.aem.sfmc.connector.core.beans.OAuthResponse;
import com.aem.sfmc.connector.core.beans.SFMCException;
import com.aem.sfmc.connector.core.services.SFMCConnectorService;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet to trigger email with the mail details in payload in request body.
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "service/sfmc/sendMail", methods = HttpConstants.METHOD_POST, extensions = "json")
@ServiceDescription("SFMC Trigger Email Servlet")
@SuppressWarnings("squid:S1948")
public class SFMCTriggerEmailServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SFMCTriggerEmailServlet.class);

	@Reference
	transient SFMCConnectorService sfmcConnectorService;

	@Reference
	transient Replicator replicator;

	
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		

		LOG.info("In SFMCTriggerEmailServlet POST method");
		handleRequest(request, response);
	}

	private void handleRequest(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {

		LOG.info("In SFMCTriggerEmailServlet handleRequest method");
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			Map<?,?> map = mapper.readValue(request.getInputStream(),Map.class);
			PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
			Page page = pageManager.getContainingPage(request.getResource());
			OAuthResponse oAuthResponse = sfmcConnectorService.getAuthTokenResponse(page);

			if (SlingHttpServletResponse.SC_OK == oAuthResponse.getStatusCode()) {
				APIResponse apiResponse = sfmcConnectorService.triggerEmail(oAuthResponse.getAccess_token(),
						oAuthResponse.getRest_instance_url(), mapper.writeValueAsString(map));
				LOG.info("Response received with status : {} and message : {}", apiResponse.getStatusCode(),
						apiResponse.getResponse());
				setServletResponse(response, apiResponse);
			} else {
				setServletResponse(response, oAuthResponse);
			}

		} catch (SFMCException e) {
			LOG.error("SFMC Exception {} : {} : {}", e.getErrorCode(), e.getErrorMsg(), e);
			response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (MethodNotSupportedException e) {
			LOG.error("SFMC Unknown Exception {} ", e);
			response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} 
	}

	private void setServletResponse(SlingHttpServletResponse response, APIResponse apiResponse) throws IOException {

		response.setStatus(apiResponse.getStatusCode());
		response.getWriter().write(apiResponse.getResponse());
	}
}
