package com.huidou.edgeinsight.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class BaseQuery {

    private int page = 1;
    private int pageSize = 20;

    public Pageable toPageable() {
        return PageRequest.of(page - 1, pageSize);
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
}
