package com.aem.sfmc.connector.core.services;

import java.util.List;
import java.util.Map;

public interface GenericListService {

	public Map<String, String> getGenericListAsMap(String listRoot, String listName);

	public List<String> getTitlesAsList(String listRoot, String listName);

}