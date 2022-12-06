package com.aem.sfmc.connector.core.workflow;

import java.io.IOException;

import javax.jcr.Session;
import javax.servlet.ServletException;

import com.aem.sfmc.connector.core.beans.OAuthResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.sfmc.connector.core.beans.APIResponse;
import com.aem.sfmc.connector.core.beans.SFMCException;
import com.aem.sfmc.connector.core.services.SFMCConnectorService;
import com.aem.sfmc.connector.core.util.SFMCConnectorUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component(service = WorkflowProcess.class, immediate = true, property = {
		"process.label=" + "Approve For Salesforce Marketing Cloud" })
public class ApproveForSalesForceMarketingCloud implements WorkflowProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApproveForSalesForceMarketingCloud.class);
	private static final String TYPE_JCR_PATH = "JCR_PATH";

	@Reference
	SFMCConnectorService sfmcConnector;

	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	Replicator replicator;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaData) {
		LOGGER.debug("ApproveForSalesForceMarketingCloud : execute() Method");
		ResourceResolver resourceResolver=null;
		try {
			WorkflowData workflowData = workItem.getWorkflowData();
			if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
				String path = workflowData.getPayload().toString();
				LOGGER.info("Approve To SalesForceMarketing Cloud triggered for Page is {} ",path);
				resourceResolver= SFMCConnectorUtil.getResourceResolver(resolverFactory, "sfmcService");
				if(null!=resourceResolver && resourceResolver.isLive()) {
					Session jcrSession=resourceResolver.adaptTo(Session.class);
					LOGGER.info("Fetched Resource Resolver from Service user");
					PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			        Page page = pageManager.getContainingPage(resourceResolver.getResource(path));
					OAuthResponse oAuthResponse = sfmcConnector.getAuthTokenResponse(page);
					APIResponse response=sfmcConnector.syncEmail(oAuthResponse.getAccess_token(), oAuthResponse.getRest_instance_url(), page, resourceResolver);
					if(null!=response) {
						LOGGER.info("Response code from SFMCConnectorService is {} and the message is{} ",response.getStatusCode(),response.getResponse());
					}
					replicator.replicate(jcrSession, ReplicationActionType.ACTIVATE, path);
				}
			}
		} catch (LoginException | ReplicationException e) {
			LOGGER.error("Error Occurred while running ApproveForSalesForceMarketingCloud Workflow: {}", e);
		}catch (SFMCException e) {
			LOGGER.error("SFMC Exception {} : {} : {}",e.getErrorCode(), e.getErrorMsg(), e);
		} catch (MethodNotSupportedException | ServletException | IOException e) {
			LOGGER.error("SFMC Unknown Exception {} ", e);
		}finally {
			SFMCConnectorUtil.closeResourceResolver(resourceResolver);
		}
		LOGGER.debug("ApproveForSalesForceMarketingCloud : execute() ended");
	}
}
