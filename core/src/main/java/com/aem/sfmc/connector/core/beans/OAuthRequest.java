package com.aem.sfmc.connector.core.beans;

import org.apache.commons.lang3.StringUtils;

public class OAuthRequest {

    private String grant_type;
    private String client_id;
    private String client_secret;
    private String account_id;

    public OAuthRequest(String client_id, String client_secret, String grant_type, String account_id) throws SFMCException {

        super();
        
        if(StringUtils.isBlank(grant_type)) {
        	grant_type = "client_credentials";
        }
        
        if(StringUtils.isNoneBlank(client_id,client_secret,grant_type,account_id)){
            this.client_id = client_id;
            this.client_secret = client_secret;
            this.grant_type = grant_type;           
            this.account_id = account_id;
        } else {
            throw new SFMCException("SFMC:INVALID_INPUT", "Required fields can't be null or empty.");
        }
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
}
