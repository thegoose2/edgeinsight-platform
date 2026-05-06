package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysRole;
import com.huidou.edgeinsight.core.repository.spi.SysRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSysRoleRepository extends JpaRepository<SysRole, Long>, SysRoleRepository {

    @Override
    List<SysRole> findAllById(Iterable<Long> ids);
}