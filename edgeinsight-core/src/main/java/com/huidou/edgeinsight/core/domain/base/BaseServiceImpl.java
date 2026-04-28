package com.huidou.edgeinsight.core.domain.base;

import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.core.repository.spi.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public abstract class BaseServiceImpl<T, ID, R extends BaseRepository<T, ID>> implements BaseService<T, ID> {

    protected abstract R getRepository();

    @Override
    public T getById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new NotFoundException("记录不存在: " + id));
    }

    @Override
    public List<T> list() {
        return getRepository().findAll();
    }

    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public T update(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public void removeById(ID id) {
        getRepository().deleteById(id);
    }

    public Page<T> list(Pageable pageable) {
        return getRepository().findAll(pageable);
    }
}
