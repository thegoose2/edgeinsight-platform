package com.huidou.edgeinsight.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class BaseQuery {
    @Min(1)
    private int pageNum  = 1;

    @Min(1)
    @Max(500)
    private int pageSize = 20;

    public Pageable toPageable() {
        return PageRequest.of(pageNum - 1, pageSize);
    }
}
