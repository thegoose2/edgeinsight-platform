package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSysPermissionRepository extends JpaRepository<SysPermission, Long> {

    List<SysPermission> findByModule(String module);

    List<SysPermission> findByIdIn(List<Long> ids);
}