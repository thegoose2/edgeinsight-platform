package com.huidou.edgeinsight.core.repository.spi;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
}
