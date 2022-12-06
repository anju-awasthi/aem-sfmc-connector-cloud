package com.aem.sfmc.connector.core.beans;

public class CreateEmailRequest {

    private String name;

    private Channels channels;

    private Category category;

    private Views views;

    private AssetType assetType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Views getViews() {
        return views;
    }

    public void setViews(Views views) {
        this.views = views;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }
}
