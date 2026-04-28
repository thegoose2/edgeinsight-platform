package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class PageResult<T> {

    private long total;
    private int page;
    private int pageSize;
    private List<T> data;

    public PageResult() {}

    public PageResult(long total, int page, int pageSize, List<T> data) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
