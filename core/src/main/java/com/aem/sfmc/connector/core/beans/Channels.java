package com.aem.sfmc.connector.core.beans;

public class Channels {

    private boolean email;

    private boolean web;

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }
}
