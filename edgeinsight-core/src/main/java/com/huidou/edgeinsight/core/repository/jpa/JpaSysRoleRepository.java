package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSysRoleRepository extends JpaRepository<SysRole, Long> {
}