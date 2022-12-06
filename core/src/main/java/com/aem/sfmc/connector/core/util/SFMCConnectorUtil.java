package com.aem.sfmc.connector.core.util;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

public final class SFMCConnectorUtil {

    private SFMCConnectorUtil() {

    }

    /**
     * Gets the resource resolver.
     *
     * @param resolverFactory the resolver factory
     * @param subService      the sub service
     * @return the resource resolver
     * @throws LoginException the login exception
     */
    @SuppressWarnings("CQRules:CQBP-72")
    public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory, String subService)
            throws LoginException {
        final Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, subService);
        
        if (resolverFactory != null) {
            return resolverFactory.getServiceResourceResolver(param);
        }
        return null;
    }

    /**
     * Close the resource resolver.
     *
     * @param resourceResolver the resource resolver
     */
    public static void closeResourceResolver(ResourceResolver resourceResolver) {
        if (resourceResolver != null && resourceResolver.isLive()) {
            resourceResolver.close();
        }
    }

    /**
     * Close the session.
     *
     * @param session the session
     */
    public static void logoutSession(Session session){

        if(null != session && session.isLive()) {
            session.logout();
        }
    }
    /**
     * Log method time.
     *
     * @param lStartTime the l start time
     * @return the long
     */
    public static long logMethodTime(long lStartTime) {
        return (System.nanoTime() - lStartTime)/1000000;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public static long getStartTime() {
        return System.nanoTime();
    }
}
