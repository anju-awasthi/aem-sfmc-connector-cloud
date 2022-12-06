package com.aem.sfmc.connector.core.beans;

public class GetFoldersResponse {

    private int count;

    private int page;

    private int pageSize;

    private Folder[] items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Folder[] getItems() {
        return items;
    }

    public void setItems(Folder[] items) {
        this.items = items;
    }
}
