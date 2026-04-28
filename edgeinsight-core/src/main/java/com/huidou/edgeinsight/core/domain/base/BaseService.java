package com.huidou.edgeinsight.core.domain.base;

import java.util.List;

public interface BaseService<T, ID> {

    T getById(ID id);

    List<T> list();

    T save(T entity);

    T update(T entity);

    void removeById(ID id);
}
