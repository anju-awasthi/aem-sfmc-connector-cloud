package com.aem.sfmc.connector.core.beans;

public enum HttpMethodType {

    /**
     * select
     */
    GET,

    /**
     * edit
     */
    POST,

    /**
     * add
     */
    PUT,

    /**
     * update
     */
    PATCH,

    /**
     * DELETE
     */
    DELETE;

	@Override
    public String toString() {
        return name();
    }
}
