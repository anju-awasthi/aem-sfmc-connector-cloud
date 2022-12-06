<%--

 *************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2014 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************

--%><%@page session="false" contentType="text/html"
            pageEncoding="utf-8"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/hideeditok.jsp"%>
<%@page session="false" import="com.adobe.granite.crypto.CryptoSupport,
						com.day.cq.i18n.I18n,
                        javax.jcr.Node,
                        org.json.JSONObject,
                        java.util.HashMap,
                        javax.jcr.Session,
                        org.apache.sling.api.resource.ValueMap"%>
<cq:includeClientLib categories="cq.personalization" />
<cq:includeClientLib categories="sfmc.cloudserviceconfigs" />
<%
    I18n i18n = new I18n(request);
    String[] successfulConfiguration = {
            i18n.get("SFMC configuration is successful. You can now communicate with SFMC on your site."),
            i18n.get("Please apply the configuration to your websites")
    };

    ValueMap sfmcValues = resource.adaptTo(ValueMap.class);
    Node sfmcConfigNode = resource.adaptTo(Node.class);
    if(sfmcValues!=null){
        CryptoSupport cryptoSupport = sling.getService(CryptoSupport.class);

    }
    String[] links=properties.get("links", String[].class);
	HashMap<String, String> linksMap = new HashMap<>();
	if(null != links && links.length > 0){
        for(String link:links){
            JSONObject jObj=new JSONObject(link);
            linksMap.put(jObj.getString("path"), jObj.getString("domain"));
        }
	}
	request.setAttribute("linksMap", linksMap);
    Session resourceSession = resource.getResourceResolver().adaptTo(Session.class);
    resourceSession.save();

    String password = properties.get("clientsecret", "");

%>
<div>
    <h3><%= i18n.get("SFMC Settings")%></h3>
    <img src="<%=thumbnailPath%>" alt="<%=serviceName%>" style="float: left;" />
    <ul style="float: left; margin: 0px;">
        <li><div class="li-bullet"><strong><%=i18n.get("Client Id: ")%></strong><%= xssAPI.encodeForHTML(properties.get("clientId", "")) %></div></li>
        <li><div class="li-bullet"><strong><%=i18n.get("Client Secret: ")%></strong><%= password.replaceAll(".", "*") %></div></li>
        <li><div class="li-bullet"><strong><%=i18n.get("Account Id: ")%></strong><%= xssAPI.encodeForHTML(properties.get("accountId", "")) %></div></li>
        <li><div class="li-bullet"><strong><%=i18n.get("SFMC OAuth URL: ")%></strong><%= xssAPI.encodeForHTML(properties.get("authUrl", "")) %></div></li>
        <li><div class="li-bullet"><strong><%=i18n.get("Email Root Folder: ")%></strong><%= xssAPI.encodeForHTML(properties.get("emailRoot", "")) %></div></li>
         <li><div class="li-bullet"><strong><%=i18n.get("Show Publish to SFMC Button: ")%></strong><%= xssAPI.encodeForHTML(properties.get("displaySFMC", "")) %></div></li>
        <c:if test="${ not empty properties.links}">
	        <li><div class="li-bullet"><strong><%=i18n.get("Externalizer Links: ")%></strong></br>
	        	<c:forEach items="${linksMap}" var="entry">
					<li><b>Path</b>: ${entry.key}<br> <b>Domain</b>: ${entry.value}</li>
                </c:forEach>
            </div></li>
        </c:if>
        <li class="config-successful-message when-config-successful" style="display: none">
            <%= xssAPI.encodeForHTML(successfulConfiguration[0]) %><br><%= xssAPI.encodeForHTML(successfulConfiguration[1]) %><a href="/siteadmin"> #.</a>
        </li>
    </ul>      
</div>
