<%@page session="false" import="com.adobe.granite.ui.components.Config,
                                    com.day.cq.wcm.api.Page,
                                    org.apache.sling.api.resource.Resource,
                                    com.adobe.granite.ui.components.rendercondition.RenderCondition,
                                    com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap,
                                    com.day.cq.commons.inherit.InheritanceValueMap,
                                    com.day.cq.wcm.webservicesupport.Configuration,
                                    com.day.cq.wcm.webservicesupport.ConfigurationManager,
                                    com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    // a condition to determine if the page contains SFMC Cloud Service Configurations

    boolean isSFMCPage = false;

    Config cfg = cmp.getConfig();
    String path = cmp.getExpressionHelper().getString(cfg.get("path", ""));
    if (path != null) {
        Resource pageResource = slingRequest.getResourceResolver().resolve(path);
        InheritanceValueMap inheritedProp = new HierarchyNodeInheritanceValueMap(pageResource);
			String[] services = inheritedProp.getInherited("cq:cloudserviceconfigs", String[].class);
			if(null==services){
				 Resource jcrRes=pageResource.getChild("jcr:content");
                 if(null!=jcrRes && jcrRes.getValueMap().containsKey("cq:cloudserviceconfigs")){
                	 services=jcrRes.getValueMap().get("cq:cloudserviceconfigs",String[].class);
                 }
			}
			if(null!=services){
			ConfigurationManager cfgMgr = slingRequest.getResourceResolver().adaptTo(ConfigurationManager.class);
			if (null != cfgMgr) {
				Configuration sfmConfiguration = cfgMgr.getConfiguration("sfmc", services);
				if (null != sfmConfiguration) {
                        Resource cloudConfig = sfmConfiguration.getContentResource();
                        if(null!=cloudConfig && cloudConfig.getValueMap().containsKey("displaySFMC")){
                        	isSFMCPage=true;
                        }
                    }
           		 }
			}
    }
    request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(isSFMCPage));
%>