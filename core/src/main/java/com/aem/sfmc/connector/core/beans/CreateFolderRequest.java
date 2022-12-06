package com.aem.sfmc.connector.core.beans;

import org.apache.commons.lang3.StringUtils;

public class CreateFolderRequest {

    private String name;

    private int parentId;

    public CreateFolderRequest(String name,int parentId) throws SFMCException {

        super();
        if(StringUtils.isNotBlank(name)){
            this.name = name;
            this.parentId = parentId;
        } else {
            throw new SFMCException("SFMC:INVALID_INPUT", "Required fields can't be null or empty.");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
