package com.aem.sfmc.connector.core.services.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.genericlists.GenericList;
import com.aem.sfmc.connector.core.constants.SFMCConstants;
import com.aem.sfmc.connector.core.services.GenericListService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(service = GenericListService.class, immediate = true)
public class GenericListServiceImpl implements GenericListService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, String> getGenericListAsMap(String listRoot, String listName) {
        final Map<String, String> genericListMap = new LinkedHashMap<>();
        final List<GenericList.Item> itemsList = getListItems(listRoot, listName);
        for (GenericList.Item item : itemsList) {
            genericListMap.put(item.getValue(), item.getTitle());
        }
        return genericListMap;
    }

    @Override
    public List<String> getTitlesAsList(String listRoot, String listName) {
        final List<String> titlesList = new ArrayList<>();

        final List<GenericList.Item> itemsList = getListItems(listRoot, listName);
        for (GenericList.Item item : itemsList) {
            titlesList.add(item.getTitle());
        }

        return titlesList;
    }

    public List<GenericList.Item> getListItems(String listRoot, String listName) {
        List<GenericList.Item> itemsList = new ArrayList<>();
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(SFMCConstants.SFMC_AUTO_INFO)) {
            final String genericlistPagePath = listRoot + listName;
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Page listPage = pageManager.getPage(genericlistPagePath);
            if (null != listPage) {
                final GenericList list = listPage.adaptTo(GenericList.class);
                itemsList = list.getItems();
            }
        } catch (final LoginException exception) {
            LOG.error("Exception getting service resource resolver ", exception);
        }
        return itemsList;
    }
}