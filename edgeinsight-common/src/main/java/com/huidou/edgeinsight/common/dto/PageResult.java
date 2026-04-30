package com.huidou.edgeinsight.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResult<T> {
    private long    total;     // 总记录数
    private List<T> list;      // 当前页数据

    public static <T> Result<PageResult<T>> ok(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return Result.ok(result);
    }

    private void setTotal(long totalElements) {
        this.total = totalElements;
    }

    private void setList(List<T> content) {
        this.list = content;
    }
}
