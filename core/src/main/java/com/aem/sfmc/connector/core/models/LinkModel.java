package com.aem.sfmc.connector.core.models;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.sfmc.connector.core.workflow.ApproveForSalesForceMarketingCloud;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class LinkModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApproveForSalesForceMarketingCloud.class);

	@Self
	SlingHttpServletRequest request;
	
	@SlingObject
	Resource resource;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ValueMapValue
	@Optional
	@Default(values = StringUtils.EMPTY)
	String link;

	@ValueMapValue
	@Optional
	@Default(values = StringUtils.EMPTY)
	String fileReference;

	@PostConstruct
	@SuppressWarnings("squid:S864")
	protected void init() {
		if (StringUtils.isNotBlank(link) || StringUtils.isNotBlank(fileReference)) {
			try {
				if (StringUtils.isNotBlank(link) && (!link.startsWith("http") || !link.contains("www"))) {
					link = link.contains(".html") ? link : (link+".html");
					LOGGER.info("Updated Link with extension :{}", link);
				}
				if (WCMMode.fromRequest(request).equals(WCMMode.DISABLED)) {
					Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
					PageManager pageManager=resourceResolver.adaptTo(PageManager.class);
					Page page=pageManager.getContainingPage(resource);
					link = getProperDomain(externalizer,page, link);
					fileReference = getProperDomain(externalizer,page, fileReference);
				}
			} catch (JSONException e) {
				LOGGER.error("Exception Occurred while parsing JSON: {}", e);
			}
		}
	}

	/**
	 * @param externalizer
	 * @param page 
	 * @throws JSONException
	 */
	private String getProperDomain(Externalizer externalizer, Page page, String href) throws JSONException {
		String properLink=href;
		if (StringUtils.isNotBlank(href)) {
			Resource pageRes=page.adaptTo(Resource.class);
			InheritanceValueMap inheritedProp = new HierarchyNodeInheritanceValueMap(pageRes);
			String[] services = inheritedProp.getInherited("cq:cloudserviceconfigs", new String[] {});
			ConfigurationManager cfgMgr = resourceResolver.adaptTo(ConfigurationManager.class);
			if (null != cfgMgr) {
				Configuration cfg = cfgMgr.getConfiguration("sfmc", services);
				if (null != cfg) {
					Resource cloudConfig = cfg.getContentResource();
						String hostName = getExternalizerFromCloudConfig(href,cloudConfig);
						if (StringUtils.isBlank(hostName)) {
							properLink=externalizer.publishLink(resourceResolver, href);
							LOGGER.info("Match not found and hence populated link Externalizer host name :{}",properLink);
						} else {
							properLink=hostName+href;
							LOGGER.info("Generated link with host name from Cloud service configuration is :{}",properLink);
						}
				}
			}
			
		}
		return properLink;
	}

	/**
	 * @param link
	 * @return
	 * @throws JSONException
	 */
	private String getExternalizerFromCloudConfig(String currentPath, Resource cloudConfig) throws JSONException {
		if(null!=cloudConfig && cloudConfig.getValueMap().containsKey("links")) {
			String[] links = cloudConfig.getValueMap().get("links", String[].class);
			if (ArrayUtils.isNotEmpty(links)) {
				for (String eachVal : links) {
					JSONObject jObj = new JSONObject(eachVal);
					if (currentPath.contains(jObj.getString("path"))) {
						return jObj.getString("domain");
					}
				}

			}
		}
		return null;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return the fileReference
	 */
	public String getFileReference() {
		return fileReference;
	}
}
