package com.aem.sfmc.connector.core.beans;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BaseRequest{

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRequest.class);

    private String url;
    private HttpMethodType methodtype;
    private String data;

    public BaseRequest(final String url, final HttpMethodType method) {
        this.url = url;
        this.methodtype = method;
    }

    public BaseRequest(final String url, final HttpMethodType method, final String data) {
        this.url = url;
        this.methodtype = method;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public HttpMethodType gethttpMethodType() {
        return methodtype;
    }

    public HttpMethod getMethod() {
        switch (methodtype) {
            case GET:
                return caseGet();
            case PUT:
                return new PutMethod(url);
            case POST:
                return new PostMethod(url);
            case DELETE:
                return new DeleteMethod(url);
            default:
                return new GetMethod(url);
        }
    }

	private HttpMethod caseGet() {
		try {
			return new GetMethod(new URI(url, false).toString());
		} catch (URIException e) {
			LOGGER.error("URIException", e);
		}
		return null;
	}


}
