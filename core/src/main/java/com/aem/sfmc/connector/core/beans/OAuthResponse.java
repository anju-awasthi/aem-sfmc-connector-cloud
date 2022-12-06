package com.aem.sfmc.connector.core.beans;

public class OAuthResponse extends APIResponse {

    private String access_token;

    private String token_type;

    private int expires_in;

    private String scope;

    private String soap_instance_url;

    private String rest_instance_url;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSoap_instance_url() {
        return soap_instance_url;
    }

    public void setSoap_instance_url(String soap_instance_url) {
        this.soap_instance_url = soap_instance_url;
    }

    public String getRest_instance_url() {
        return rest_instance_url;
    }

    public void setRest_instance_url(String rest_instance_url) {
        this.rest_instance_url = rest_instance_url;
    }
}
