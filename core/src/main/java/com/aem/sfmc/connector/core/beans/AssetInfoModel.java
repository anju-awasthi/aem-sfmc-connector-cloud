package com.aem.sfmc.connector.core.beans;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class AssetInfoModel {

	@Inject
	@Named("jcr:created")
	private String createdDate;

	@Inject
	@Named("jcr:createdBy")
	private String createdBy;

	@Inject
	@Named("jcr:content/jcr:lastModified")
	@Optional
	private String lastModified;

	@Inject
	@Named("jcr:content/metadata/dc:title")
	@Optional
	private String title;

	@Inject
	@Named("jcr:content/metadata/dc:description")
	@Optional
	private String description;

	@Inject
	@Named("jcr:content/metadata/dc:format")
	@Optional
	private String format;

	private String path;

	@PostConstruct
	protected void init() {

	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
