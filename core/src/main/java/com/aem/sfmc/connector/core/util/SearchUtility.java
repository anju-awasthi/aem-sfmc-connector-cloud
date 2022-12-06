package com.aem.sfmc.connector.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = SearchUtility.class, immediate = true)
public class SearchUtility {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Reference
	private QueryBuilder builder;
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	public List<String> getResultPathList(final Map<String, String> queryMap) {
		List<String> hits = new ArrayList<>();
		try {
			final SearchResult result = getSearchResult(queryMap);
			if (result != null) {
				for (Hit results : result.getHits()) {
					hits.add(results.getPath());

				}
			}
		} catch (RepositoryException e) {
			log.error("Exception while executing search {}", e);
		}
		return hits;
	}

	public SearchResult getSearchResult(final Map<String, String> queryMap) {
		Session session = null;
		SearchResult searchResult = null;
		try (ResourceResolver resourceResolver = SFMCConnectorUtil.getResourceResolver(resourceResolverFactory,
				SFMCConnectorConstants.SUB_SERVICE_NAME)) {
			session = resourceResolver.adaptTo(Session.class);
			log.info("Running Query : {}", PredicateGroup.create(queryMap).toString());
			final Query query = builder.createQuery(PredicateGroup.create(queryMap), session);
			searchResult = query.getResult();
		} catch (LoginException loginException) {
			log.error("Exception while executing search {}", loginException);
		} finally {
			if (null != session && session.isLive())
				session.logout();
		}
		return searchResult;
	}

}
