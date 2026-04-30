package com.huidou.edgeinsight.core.domain.base;

import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.model.base.BaseEntity;
import com.huidou.edgeinsight.core.repository.spi.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public abstract class BaseServiceImpl<T extends BaseEntity,
        ID,
        R extends JpaRepository<T, ID>>
        implements BaseService<T, ID> {

    protected final R repository;

    protected BaseServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("记录不存在: " + id));
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        getById((ID) entity.getId()); // 先确认存在
        return repository.save(entity);
    }

    @Override
    public void removeById(ID id) {
        getById(id); // 先确认存在
        repository.deleteById(id);
    }
}