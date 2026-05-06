package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.SysRole;

import java.util.List;

public interface SysRoleRepository {

    List<SysRole> findAllById(Iterable<Long> ids);

    List<SysRole> findAll();
}